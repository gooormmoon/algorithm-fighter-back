package gooroommoon.algofi_core.game.session;


import gooroommoon.algofi_core.algorithmproblem.AlgorithmproblemService;
import gooroommoon.algofi_core.auth.member.MemberService;
import gooroommoon.algofi_core.chat.dto.MessageDTO;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.entity.MessageType;
import gooroommoon.algofi_core.chat.repository.ChatroomRepository;
import gooroommoon.algofi_core.chat.service.ChatService;
import gooroommoon.algofi_core.game.session.dto.*;
import gooroommoon.algofi_core.game.session.exception.*;
import gooroommoon.algofi_core.gameresult.GameresultService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class GameSessionService {

    //유저가 채팅방에 새로 들어올 때 입장 메시지 보내는 메서드 호출하기

    private final Map<String, GameSession> gameSessions = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry userRegistry;
    private final WebClient webClient;

    private final ChatroomRepository chatRoomRepository;
    private final MemberService memberService;

    private final ChatService chatService;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private final AlgorithmproblemService algorithmproblemService;
    private final GameresultService gameresultService;

    public GameSession getSession(String playerId) {
        GameSession session = gameSessions.get(playerId);
        if(session == null) {
            throw new GameSessionNotFoundException("게임을 찾을 수 없습니다.");
        }
        return session;
    }

    public GameSessionsResponse getSessionsResponse() {
        GameSessionsResponse response = new GameSessionsResponse(new ArrayList<>());
        Set<GameSession> sessions = new HashSet<>(gameSessions.values());

        sessions.forEach(session ->
            response.getRooms().add(GameSessionsResponse.Session.builder()
                    .host(memberService.getMemberNickName(session.getHostId()))
                    .hostId(session.getHostId())
                    .title(session.getTitle())
                    .players(session.getPlayers())
                    .maxPlayer(session.getMaxPlayer())
                    .problemLevel(session.getProblemLevel())
                    .timerTime(session.getTimerTime())
                    .isStarted(session.isStarted())
                    .build()));

        return response;
    }

    public void sendSessions(String loginId) {
        messagingTemplate.convertAndSendToUser(loginId, "/queue/game/sessions", getSessionsResponse());
    }

    public void sendSessions() {
        GameSessionsResponse response = getSessionsResponse();
        userRegistry.getUsers().forEach(user -> {
            String playerId = user.getPrincipal().getName();
            if(!gameSessions.containsKey(playerId)) {
                messagingTemplate.convertAndSendToUser(playerId, "/queue/game/sessions", response);
            }
        });
    }

    public void createSession(String hostId, GameSessionUpdateRequest request) {
        checkPlayerInGame(hostId);
        GameSession session = new GameSession(hostId, request.getTitle(), request.getProblemLevel(), request.getTimerTime());
        gameSessions.put(hostId, session);
        Chatroom chatroom = new Chatroom(session.getChatroomId(), hostId);
        chatRoomRepository.save(chatroom);
        chatService.enterRoom(session.getChatroomId(), hostId);

        sendUpdateToPlayers(session, "join");
        sendSessions();
    }

    public void addPlayer(String hostId, String playerId) {
        checkPlayerInGame(playerId);
        GameSession session = getSession(hostId);
        session.addPlayer(playerId);
        gameSessions.put(playerId, session);
        chatService.enterRoom(session.getChatroomId(), playerId);

        sendUpdateToPlayers(session, "join");
        sendUpdateToPlayers(session, "session");
        sendSessions();
    }

    public void updateSetting(String hostId, GameSessionUpdateRequest request) {
        GameSession session = getSession(hostId);
        if(session.getHostId().equals(hostId)) {
            session.updateSettings(request.getTitle(), request.getProblemLevel(), request.getTimerTime());

            sendUpdateToPlayers(session, "session");
        } else {
            throw new NotAHostException("방장만 게임 설정을 변경할 수 있습니다.");
        }
    }

    public void playerReady(String playerId) {
        GameSession session = getSession(playerId);
        session.addReadyPlayer(playerId);

        sendUpdateToPlayers(session, "session");
    }

    public void startGame(String hostId) {
        GameSession session = getSession(hostId);
        if(!session.allReady()) {
            throw new PlayersNotReadyException("준비되지 않은 플레이어가 있습니다.");
        }
        if(!session.getHostId().equals(hostId)) {
            throw new NotAHostException("방장만 게임을 시작할 수 있습니다.");
        }

        session.start(algorithmproblemService.getRandom(session.getProblemLevel()));
        GameSessionStartResponse startResponse = new GameSessionStartResponse(session.getTimerTime(),
                algorithmproblemService.toResponse(session.getAlgorithmProblem()));
        session.getPlayersStream().forEach(id -> {
            messagingTemplate.convertAndSendToUser(id, "/queue/game/start", startResponse);
        });

        sendSessions();

        //타이머 태스크 등록
        GameSessionService gameSessionService = this;
        ScheduledFuture<?> timeOverTask = executorService.schedule(() ->
            gameSessionService.closeGame(session,null)
        , session.getTimerTime(), TimeUnit.SECONDS);
        session.setTimeOverTask(timeOverTask);
    }

    public void submitCode(String playerId, GameCodeRequest request) {
        GameSession session = getSession(playerId);
        if(!session.isStarted()) {
            throw new GameIsNotStartedException("게임이 아직 시작되지 않았습니다.");
        }

        sendChat(session.getChatroomId(), playerId, "코드를 제출했습니다!");
        SubmitCodeRequest submitRequest = new SubmitCodeRequest(session.getAlgorithmProblem().getAlgorithmproblemId(), request.getLanguage(), request.getCode());
        webClient.post()
                .uri("/api/judge-problem")
                .body(Mono.just(submitRequest), SubmitCodeRequest.class)
                .retrieve()
                .toEntity(SubmitResultResponse.class)
                .subscribe(response -> {
                    String message = response.getBody().getMessage();
                    messagingTemplate.convertAndSendToUser(playerId, "/queue/game/result", new SubmitResultResponse(message));
                    if(message.equals("맞았습니다.")) {
                        closeGame(session, playerId);
                    } else {
                        sendChat(session.getChatroomId(), playerId, "틀렸습니다!");
                    }
                });
    }

    public void closeGame(GameSession session, String winnerId) {
        int runningTime = (int) (System.currentTimeMillis() - session.getStartTime());
        session.setRunningTime(runningTime);
        if(winnerId != null) {
            GameSessionOverResponse winnerResponse = new GameSessionOverResponse(GameOverType.WIN, runningTime);
            GameSessionOverResponse loserResponse = new GameSessionOverResponse(GameOverType.LOSE, runningTime);
            session.setWinnerId(winnerId);
            messagingTemplate.convertAndSendToUser(
                    winnerId, "/queue/game/over", winnerResponse
            );
            session.getPlayersStream().forEach(id -> {
                if(!id.equals(winnerId))
                    messagingTemplate.convertAndSendToUser(id, "/queue/game/over", loserResponse);
            });
            session.getTimeOverTask().cancel(true);
        } else {
            GameSessionOverResponse timeOverResponse = new GameSessionOverResponse(GameOverType.TIME_OVER, runningTime);
            session.getPlayersStream().forEach(id ->
                messagingTemplate.convertAndSendToUser(id, "/queue/game/over", timeOverResponse));
        }
    }
    //게임이 종료된 후 클라이언트의 요청을 받아 코드를 저장
    public void savePlayerCode(String playerId, GameCodeRequest request) {

        GameSession session = getSession(playerId);
        if(!session.isStarted()) {
            throw new GameIsNotStartedException("게임이 아직 시작되지 않았습니다.");
        }
            if (playerId.equals(session.getHostId())) {
                session.setHostGameCode(request.getCode());
                session.setHostCodeLanguage(request.getLanguage());
            } else {
                session.setOtherGameCode(request.getCode());
                session.setGuestCodeLanguage(request.getLanguage());
            }
            //양 측의 코드가 저장됐으면 DB에 저장
            if (session.getHostGameCode() != null && session.getOtherGameCode() != null) {
                saveResult(session);
            }

    }

    private void saveResult(GameSession session) {
        gameresultService.save(session.getChatroomId(),
                session.getPlayers(),
                session.getHostGameCode(),
                session.getOtherGameCode(),
                session.getAlgorithmProblem(),
                session.getRunningTime(),
                session.getHostId(),
                session.getHostCodeLanguage(),
                session.getGuestCodeLanguage(),
                session.getWinnerId());

        removeSession(session);
    }

    public void removeSession(GameSession session) {
        session.getPlayersStream().forEach(gameSessions::remove);
        sendSessions();
    }

    private void checkPlayerInGame(String playerId) {
        if(gameSessions.get(playerId) != null) {
            throw new AlreadyInGameSessionException("이미 게임에 참가 중입니다.");
        }
    }

    private void sendUpdateToPlayers(GameSession session, String destination) {
        session.getPlayersStream().forEach(id ->
            messagingTemplate.convertAndSendToUser(
                    id, "/queue/game/" + destination, toResponse(session)
            )
        );
    }

    private void sendChat(String chatroomId, String senderId, String content) {
        MessageDTO chat = MessageDTO.builder()
                .type(MessageType.TALK)
                .chatroomId(chatroomId)
                .content(content)
                .senderId(senderId)
                .createdDate(LocalDateTime.now())
                .build();
        messagingTemplate.convertAndSend("/topic/room/" + chatroomId, chat);
    }

    public GameSessionResponse toResponse(GameSession session) {
        String hostNick = memberService.getMemberNickName(session.getHostId());

        return GameSessionResponse.builder()
                .title(session.getTitle())
                .host(hostNick)
                .hostId(session.getHostId())
                .players(session.getPlayers())
                .readyPlayers(session.getReadyPlayers())
                .maxPlayer(session.getMaxPlayer())
                .problemLevel(session.getProblemLevel())
                .timerTime(session.getTimerTime())
                .chatroomId(session.getChatroomId())
                .build();
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        if(event.getUser() == null || gameSessions.get(event.getUser().getName()) == null) {
            return;
        }

        String playerId = event.getUser().getName();
        GameSession session = getSession(playerId);
        if(playerId.equals(session.getHostId())) {
            removeSession(session);
            //TODO 참가중인 모든 플레이어에게 방 삭제 메시지 발행
        } else {
            session.removePlayer(playerId);
            sendUpdateToPlayers(session, "session");
            sendSessions();
        }

        // 퇴장 메시지 발행
        String leaveMessage = playerId + "님이 퇴장하셨습니다.";
        MessageDTO message = MessageDTO.builder()
                .type(MessageType.LEAVE)
                .chatroomId(session.getChatroomId())
                .content(leaveMessage)
                .build();
        messagingTemplate.convertAndSend("/topic/room/" + session.getChatroomId(), message);
    }
}
