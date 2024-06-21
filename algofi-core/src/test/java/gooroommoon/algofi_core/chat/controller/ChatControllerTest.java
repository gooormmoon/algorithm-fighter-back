package gooroommoon.algofi_core.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.auth.member.MemberRepository;
import gooroommoon.algofi_core.auth.member.MemberRole;
import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.TokenResponse;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.entity.Message;
import gooroommoon.algofi_core.chat.entity.MessageType;
import gooroommoon.algofi_core.chat.dto.MessageDTO;
import gooroommoon.algofi_core.chat.repository.ChatroomRepository;
import gooroommoon.algofi_core.chat.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class ChatControllerTest {

    @LocalServerPort
    private int port;
    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private String token;
    private String chatroomId;
    private Member members;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatroomRepository chatroomRepository;

    @BeforeEach
    public void setup() throws Exception {
        // DB에 저장되어 있는 인증된 유저의 토큰 가져오기 No value present -> 아래 줄 주석 처리하고 진행
//        memberRepository.delete(memberRepository.findByLoginId("user").get());

        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        Member member = Member.builder()
                .name("A")
                .nickname("A")
                .loginDate(LocalDateTime.now())
                .loginId("user")
                .password("pass")
                .role(MemberRole.USER)
                .build();

        members = member;
        memberRepository.save(member);

        MemberRequest.LoginRequest loginRequest = MemberRequest.LoginRequest.builder()
                .id("user").password("pass").build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MemberRequest.LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<TokenResponse> response = testRestTemplate.postForEntity("/api/member/login", request, TokenResponse.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Failed to retrieve access token. HTTP Status: " + response.getStatusCode());
        }

        TokenResponse tokenResponse = response.getBody();
        token = tokenResponse.getAccessToken();
        chatroomId = UUID.randomUUID().toString();
        Chatroom chatroom = new Chatroom(chatroomId, members.getLoginId());
        chatroomRepository.save(chatroom);
    }

    @Test
    @DisplayName("채팅방 입장 WebSocket 통합 테스트")
    public void testEnterRoom() throws Exception {
        System.out.println("TOKEN : " + token);

        // WebSocket setup
        String url = String.format("ws://localhost:%d/chat", port);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        stompSession = stompClient
                .connect(url, headers, new StompSessionHandlerAdapter() {})
                .get(1, TimeUnit.SECONDS);

        stompSession.subscribe("/topic/room/" + chatroomId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return null;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {

            }
        });

        // enterRoom Service test
        Chatroom chatrooms = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new EntityNotFoundException("Chatroom not found with id: " + chatroomId));

        // Save a message
        Message message = Message.builder()
                .chatroomId(chatrooms)
                .senderId(members)
                .type(MessageType.ENTER)
                .content(members.getNickname() + "님이 입장하셨습니다.")
                .createdDate(LocalDateTime.now())
                .build();

        messageRepository.save(message);

        MessageDTO messageDTO = MessageDTO.builder()
                .type(message.getType())
                .chatroomId(message.getChatroomId().getChatroomId())
                .content(message.getContent())
                .build();

        StompHeaders sendJoinHeaders = new StompHeaders();
        sendJoinHeaders.setDestination("/app/enter-room/" + chatroomId);
        sendJoinHeaders.add("Authorization", "Bearer " + token);

        stompSession.send(sendJoinHeaders, messageDTO);
        System.out.println("Message sent: " + messageDTO.getContent());
        // TODO 웹소켓 메시지 확인 관련 부분은 resource.chat에서 확인하기
    }
}

