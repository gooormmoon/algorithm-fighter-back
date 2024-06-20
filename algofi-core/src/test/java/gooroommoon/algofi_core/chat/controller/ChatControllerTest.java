package gooroommoon.algofi_core.chat.controller;

import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.auth.member.MemberRepository;
import gooroommoon.algofi_core.chat.entity.MessageType;
import gooroommoon.algofi_core.chat.dto.MessageDTO;
import gooroommoon.algofi_core.chat.repository.MessageRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


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
                assertEquals(roomId, messageDTO.getChatRoomId());
                assertEquals("testUser님이 입장하셨습니다.", messageDTO.getContent());
                assertEquals(MessageType.ENTER, messageDTO.getType());
            }
        });

        // Send message to WebSocket endpoint
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setType(MessageType.ENTER);
        messageDTO.setChatRoomId(roomId);
        messageDTO.setContent("testUser님이 입장하셨습니다.");

        stompSession.send("/app/enter-room/"+ roomId.toString(), messageDTO);

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
        // 연결하는 순간에 유효한 토큰을 넣어야 인증이 가능함.
        // DB에 유저가 없으면 안됨.
        // 회원가입, 로그인 코드 넣고 토큰 가져와서 사용하기
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
//        stompSession.disconnect();
    }
}

