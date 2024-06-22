package gooroommoon.algofi_core.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .nullDestMatcher().permitAll()
                .simpSubscribeDestMatchers("/user/queue/game/session",
                        "/user/queue/game/sessions",
                        "/user/queue/game/join",
                        "/user/queue/game/start",
                        "/user/queue/game/result",
                        "/user/queue/game/over").authenticated()
                .simpSubscribeDestMatchers("/topic/room/*").permitAll()
                .simpDestMatchers("/app/**").authenticated()
                .anyMessage().denyAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
