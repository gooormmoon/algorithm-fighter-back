package gooroommoon.algofi_core.game.gamesession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private GameRoomRepository gameRoomRepository;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Create and register GameChatHandler as a Spring bean
        GameChatHandler chatHandler = new GameChatHandler(gameRoomRepository);
        registry.addHandler(chatHandler, "/ws/api").withSockJS();
    }

}
