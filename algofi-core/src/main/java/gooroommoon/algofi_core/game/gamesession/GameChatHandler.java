package gooroommoon.algofi_core.game.gamesession;

import com.fasterxml.jackson.databind.ObjectMapper;
import gooroommoon.algofi_core.game.exception.GameRoomNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class GameChatHandler extends TextWebSocketHandler {

    ObjectMapper objectMapper = new ObjectMapper();
    private final GameRoomRepository gameRoomRepository;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        GameChatMessage chatMessage = objectMapper.readValue(payload, GameChatMessage.class);
        GameRoom gameRoom = gameRoomRepository.findByGameCode(chatMessage.getGameCode())
                .orElseThrow(() -> new GameRoomNotFoundException("게임방을 찾을 수 없습니다."));
        gameRoom.handleMessage(session, chatMessage, objectMapper);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        gameRoomRepository.remove(session);

    }

}