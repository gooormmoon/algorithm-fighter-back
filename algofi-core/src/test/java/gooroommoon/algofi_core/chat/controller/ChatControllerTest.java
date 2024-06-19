package gooroommoon.algofi_core.chat.controller;

import gooroommoon.algofi_core.chat.entity.MessageType;
import gooroommoon.algofi_core.chat.dto.MessageDTO;
import gooroommoon.algofi_core.chat.repository.MessageRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// TEST를 진행하기 전에 수정해야할 것
// SecurityConfig 에서 .anyRequest().permitAll() 로 수정

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class ChatControllerTest {

    @Autowired
    MessageRepository messageRepository;

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private final String WEBSOCKET_TOPIC = "/topic/room/";

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    @DisplayName("채팅방 입장 WebSocket 통합 테스트")
    public void testEnterRoomWebSocket() throws Exception {
        // WebSocket 연결 설정
        String websocketUri = "ws://localhost:" + port + "/chat";
        StompSession stompSession = stompClient.connect(websocketUri, new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);

        // Subscribe to the WebSocket topic
        UUID roomId = UUID.randomUUID();
        StompHeaders headers = new StompHeaders();
        headers.setDestination(WEBSOCKET_TOPIC + roomId.toString());
        headers.set("username", "testUser");
        stompSession.subscribe(headers, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                MessageDTO messageDTO = (MessageDTO) payload;
                assertEquals("TestUser님이 입장하셨습니다.", messageDTO.getContent());
                assertEquals(MessageType.ENTER, messageDTO.getType());
            }
        });

        // Send message to WebSocket endpoint
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setType(MessageType.ENTER);
        messageDTO.setChatRoomId(roomId);
        messageDTO.setContent("TestUser님이 입장하셨습니다.");

        stompSession.send("/app/enter-room/1", messageDTO);

        // Wait for response
        Thread.sleep(1000);

        // Disconnect from WebSocket
        stompSession.disconnect();
    }

    @Test
    @DisplayName("채팅 송수신 WebSocket 통합 테스트")
    public void testSendMessage() throws Exception {
        // WebSocket 연결 설정
        String websocketUri = "ws://localhost:" + port + "/chat";
        StompSession stompSession = stompClient.connect(websocketUri, new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);

        // Subscribe to the WebSocket topic
        UUID roomId = UUID.randomUUID();
        StompHeaders headers = new StompHeaders();
        headers.setDestination(WEBSOCKET_TOPIC + roomId.toString());
        stompSession.subscribe(headers, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                MessageDTO messageDTO = (MessageDTO) payload;
                assertEquals("Test message content", messageDTO.getContent());
                assertEquals(MessageType.TALK, messageDTO.getType());
            }
        });

        // Send message to WebSocket endpoint
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setType(MessageType.TALK);
        messageDTO.setChatRoomId(roomId);
        messageDTO.setContent("Test message content");

        stompSession.send("/app/send-message", messageDTO);

        // Wait for response
        Thread.sleep(1000);

        // Disconnect from WebSocket
        stompSession.disconnect();
    }
}

