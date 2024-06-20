package gooroommoon.algofi_core.game.session;


import gooroommoon.algofi_core.algorithmproblem.AlgorithmproblemService;
import gooroommoon.algofi_core.chat.dto.MessageDTO;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.entity.MessageType;
import gooroommoon.algofi_core.chat.repository.ChatRoomRepository;
import gooroommoon.algofi_core.chat.service.ChatService;
import gooroommoon.algofi_core.game.session.dto.GameSessionOverResponse;
import gooroommoon.algofi_core.game.session.dto.GameSessionUpdateRequest;
import gooroommoon.algofi_core.game.session.exception.AlreadyInGameSessionException;
import gooroommoon.algofi_core.game.session.exception.GameSessionNotFoundException;
import gooroommoon.algofi_core.game.session.exception.NotAHostException;
import gooroommoon.algofi_core.game.session.exception.PlayersNotReadyException;
import gooroommoon.algofi_core.gameresult.GameresultService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class GameSessionService {

    //유저가 채팅방에 새로 들어올 때 입장 메시지 보내는 메서드 호출하기

    private final Map<String, GameSession> gameSessions = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    private final ChatRoomRepository chatRoomRepository;

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

    public void createSession(String hostId, GameSessionUpdateRequest request) {
        checkPlayerInGame(hostId);
        GameSession session = new GameSession(hostId, request.getTitle(), request.getProblemLevel(), request.getTimerTime());
        gameSessions.put(hostId, session);
        Chatroom chatroom = new Chatroom(session.getChatRoomId(), hostId);
        chatRoomRepository.save(chatroom);
        chatService.enterRoom(session.getChatRoomId(), hostId);

        sendUpdateToPlayers(session);
    }

    public void addPlayer(String hostId, String playerId) {
        checkPlayerInGame(playerId);
        GameSession session = getSession(hostId);
        session.addPlayer(playerId);
        gameSessions.put(playerId, session);
        chatService.enterRoom(session.getChatRoomId(), playerId);

        sendUpdateToPlayers(session);
    }

    public void updateSetting(String hostId, GameSessionUpdateRequest request) {
        GameSession session = getSession(hostId);
        if(session.getHost().equals(hostId)) {
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
        if(!session.getHost().equals(hostId)) {
            throw new NotAHostException("방장만 게임을 시작할 수 있습니다.");
        }
        //TODO 문제 가져와서 넣기
        //랜덤 알고리즘문제
        algorithmproblemService.getRandom(session.getProblemLevel());
        session.start();
        //TODO 알고리즘 문제 가져와서 메시지 발행
        GameSessionService gameSessionService = this;
        ScheduledFuture<?> timeOverTask = executorService.schedule(() -> {
            gameSessionService.closeGame(session,null, session.getTimerTime());
        }, session.getTimerTime(), TimeUnit.SECONDS);
        session.setTimeOverTask(timeOverTask);
    }

    public void submitCode(String playerId) {
        //TODO 제출된 코드 실행 및 결과 받기
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
        //TODO 게임 결과 저장
        //TODO gameSession에서 필요한 것 다 가져오기
        gameresultService.save(session, runningTime);
        removeSession(session);
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
                    id, "/queue/game/session", session.toResponse()
            );
        });
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        if(event.getUser() == null || gameSessions.get(event.getUser().getName()) == null) {
            return;
        }

        String playerId = event.getUser().getName();
        GameSession session = getSession(playerId);
        session.removePlayer(playerId);
        if(playerId.equals(session.getHost())) {
            removeSession(session);
        }
        //TODO 방 삭제 메시지 발행

        // 퇴장 메시지 발행
        String leaveMessage = playerId + "님이 퇴장하셨습니다.";
        MessageDTO message = MessageDTO.builder()
                .type(MessageType.LEAVE)
                .chatroomId(session.getChatRoomId())
                .content(leaveMessage)
                .build();
        messagingTemplate.convertAndSend("/topic/room/" + session.getChatRoomId(), message);
    }
}
