package gooroommoon.algofi_core.game.session;


import gooroommoon.algofi_core.algorithmproblem.AlgorithmproblemService;
import gooroommoon.algofi_core.auth.member.MemberRepository;
import gooroommoon.algofi_core.auth.member.MemberService;
import gooroommoon.algofi_core.chat.dto.MessageDTO;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.entity.MessageType;
import gooroommoon.algofi_core.chat.repository.ChatroomRepository;
import gooroommoon.algofi_core.chat.service.ChatService;
import gooroommoon.algofi_core.game.session.dto.*;
import gooroommoon.algofi_core.game.session.exception.AlreadyInGameSessionException;
import gooroommoon.algofi_core.game.session.exception.GameSessionNotFoundException;
import gooroommoon.algofi_core.game.session.exception.NotAHostException;
import gooroommoon.algofi_core.game.session.exception.PlayersNotReadyException;
import gooroommoon.algofi_core.gameresult.GameresultService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class GameSessionService {

    //유저가 채팅방에 새로 들어올 때 입장 메시지 보내는 메서드 호출하기

    private final Map<String, GameSession> gameSessions = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;
    private final SimpUserRegistry userRegistry;

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

        sessions.stream().forEach(session -> {
            response.getRooms().add(GameSessionsResponse.Session.builder()
                    .host(memberService.getMemberNickName(session.getHostId()))
                    .hostId(session.getHostId())
                    .title(session.getTitle())
                    .maxPlayer(session.getMaxPlayer())
                    .problemLevel(session.getProblemLevel())
                    .timerTime(session.getTimerTime())
                    .isStarted(session.isStarted())
                    .build());
        });

        return response;
    }

    public void sendSessions(String loginId) {
        messagingTemplate.convertAndSendToUser(loginId, "/queue/game/session", getSessionsResponse());
    }

    public void sendSessions() {
        GameSessionsResponse response = getSessionsResponse();
        userRegistry.getUsers().stream().forEach(user -> {
            String playerId = user.getPrincipal().getName();
            if(!gameSessions.containsKey(playerId)) {
                messagingTemplate.convertAndSendToUser(playerId, "/queue/game/session", response);
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

        sendUpdateToPlayers(session);
    }

    public void addPlayer(String hostId, String playerId) {
        checkPlayerInGame(playerId);
        GameSession session = getSession(hostId);
        session.addPlayer(playerId);
        gameSessions.put(playerId, session);
        chatService.enterRoom(session.getChatroomId(), playerId);

        sendUpdateToPlayers(session);
    }

    public void updateSetting(String hostId, GameSessionUpdateRequest request) {
        GameSession session = getSession(hostId);
        if(session.getHostId().equals(hostId)) {
            session.updateSettings(request.getTitle(), request.getProblemLevel(), request.getTimerTime());

            sendUpdateToPlayers(session);
        } else {
            throw new NotAHostException("방장만 게임 설정을 변경할 수 있습니다.");
        }
    }

    public void playerReady(String playerId) {
        GameSession session = getSession(playerId);
        session.addReadyPlayer(playerId);

        sendUpdateToPlayers(session);
    }

    public void startGame(String hostId) {
        GameSession session = getSession(hostId);
        if(!session.allReady()) {
            throw new PlayersNotReadyException("준비되지 않은 플레이어가 있습니다.");
        }
        if(!session.getHostId().equals(hostId)) {
            throw new NotAHostException("방장만 게임을 시작할 수 있습니다.");
        }
        //TODO 문제 가져와서 넣기
        //랜덤 알고리즘문제
        algorithmproblemService.getRandom(session.getProblemLevel());
        session.start();
        //TODO 알고리즘 문제 가져와서 메시지 발행
        //TODO 러닝타임 기록
        GameSessionService gameSessionService = this;
        ScheduledFuture<?> timeOverTask = executorService.schedule(() -> {
            gameSessionService.closeGame(session,null, session.getTimerTime());
        }, session.getTimerTime(), TimeUnit.SECONDS);
        session.setTimeOverTask(timeOverTask);
    }

    public void submitCode(String playerId, GameCodeRequest request) {
        //TODO 제출된 코드 실행 및 결과 받기
        //TODO 제출했습니다 메시지 전송
        GameSession session = getSession(playerId);

    }

    public void closeGame(GameSession session, String winnerId, int runningTime) {
        if(winnerId != null) {
            GameSessionOverResponse winnerResponse = new GameSessionOverResponse(GameOverType.WIN, runningTime);
            GameSessionOverResponse loserResponse = new GameSessionOverResponse(GameOverType.LOSE, runningTime);
            messagingTemplate.convertAndSendToUser(
                    winnerId, "/queue/game/session", winnerResponse
            );
            session.getPlayersStream().forEach(id -> {
                if(!id.equals(winnerId))
                    messagingTemplate.convertAndSendToUser(id, "/queue/game/session", loserResponse);
            });
            session.getTimeOverTask().cancel(true);
        } else {
            GameSessionOverResponse timeOverResponse = new GameSessionOverResponse(GameOverType.TIME_OVER, runningTime);
            session.getPlayersStream().forEach(id -> {
                messagingTemplate.convertAndSendToUser(id, "/queue/game/session", timeOverResponse);
            });
        }
    }

    public void savePlayerCode(String playerId, GameCodeRequest request) {
        GameSession session = getSession(playerId);
        if(playerId.equals(session.getHostId())) {
            session.setHostGameCode(request.getCode());
        } else {
            session.setOtherGameCode(request.getCode());
        }

        if(session.getHostGameCode() != null && session.getOtherGameCode() != null) {
            saveResult(session);
        }
    }

    private void saveResult(GameSession session) {
        //TODO 게임 결과 저장
        //TODO 게임 세션 삭제
    }

    public void removeSession(GameSession session) {
        session.getPlayersStream().forEach(gameSessions::remove);
    }

    private void checkPlayerInGame(String playerId) {
        if(gameSessions.get(playerId) != null) {
            throw new AlreadyInGameSessionException("이미 게임에 참가 중입니다.");
        }
    }

    private void sendUpdateToPlayers(GameSession session) {
        session.getPlayersStream().forEach(id -> {
            messagingTemplate.convertAndSendToUser(
                    id, "/queue/game/session", toResponse(session)
            );
        });
    }

    public GameSessionResponse toResponse(GameSession session) {
        String hostNick = memberService.getMemberNickName(session.getHostId());
        Set<String> players = new HashSet<>();
        Set<String> readyPlayers = new HashSet<>();

        session.getPlayersStream().forEach(playerId -> {
            String playerNick = memberService.getMemberNickName(playerId);
            players.add(playerNick);
            if(session.getReadyPlayers().contains(playerId)) {
                readyPlayers.add(playerNick);
            }
        });

        return GameSessionResponse.builder()
                .title(session.getTitle())
                .host(hostNick)
                .hostId(session.getHostId())
                .players(players)
                .readyPlayers(readyPlayers)
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
        session.removePlayer(playerId);
        if(playerId.equals(session.getHostId())) {
            removeSession(session);
            //TODO 참가중인 모든 플레이어에게 방 삭제 메시지 발행
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
