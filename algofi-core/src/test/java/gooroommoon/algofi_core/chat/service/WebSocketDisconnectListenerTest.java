package gooroommoon.algofi_core.chat.service;

import gooroommoon.algofi_core.chat.dto.MessageDTO;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.entity.MessageType;
import gooroommoon.algofi_core.chat.repository.ChatRoomRepository;
import gooroommoon.algofi_core.chat.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WebSocketDisconnectListenerTest {

    @Autowired
    private ChatService chatService;

    @MockBean
    private ChatRoomRepository chatRoomRepository;

    @MockBean
    private SimpMessagingTemplate template;

    @Test
    @DisplayName("WebSocket이 끊겼을 때")
    public void testHandleWebSocketDisconnectListener() throws Exception {
        // 가짜 Principal 객체 생성
        Principal principal = new TestingAuthenticationToken("user1", "password");

        // 가짜 SimpMessageHeaderAccessor 객체 생성
        SimpMessageHeaderAccessor simpAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.DISCONNECT);
        simpAccessor.setUser(principal);

        // 가짜 WebSocket 메시지 생성
        byte[] payload = "Disconnect message".getBytes();
        Message<byte[]> message = new GenericMessage<>(payload, simpAccessor.getMessageHeaders());

        // WebSocket 세션 ID 추출
        String sessionId = simpAccessor.getSessionId();

        // WebSocket 종료 상태 추출
        CloseStatus closeStatus = new CloseStatus(CloseStatus.NORMAL.getCode(), "Normal disconnect");

        // Mock SessionDisconnectEvent 생성
        SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);
        given(event.getMessage()).willReturn(message);
        given(event.getSessionId()).willReturn(sessionId);
        given(event.getCloseStatus()).willReturn(closeStatus);
        given(event.getUser()).willReturn(principal);

        // 가짜 Chatroom 객체 생성
        Chatroom chatRoom = new Chatroom();
        chatRoom.setChatroomId(UUID.randomUUID());
        chatRoom.setChatroomName("user1");

        // chatRoomRepository mock 객체 설정
        given(chatRoomRepository.findByChatroomName("user1")).willReturn(Optional.of(chatRoom));

        // chatRoomRepository.delete 메서드가 호출 시 동작 설정
        doNothing().when(chatRoomRepository).delete(chatRoom);

        // 테스트할 메서드 호출
        chatService.handleWebSocketDisconnectListener(event);

        // chatRoomRepository.delete 메서드가 1회 호출되었는지 검증
        verify(chatRoomRepository, times(1)).delete(chatRoom);

        // chatRoomRepository.delete 메서드가 1회 호출되었는지 검증
        verify(chatRoomRepository, times(1)).delete(chatRoom);

        // template.convertAndSend 메서드가 1회 호출되었는지 검증
        ArgumentCaptor<String> destinationCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MessageDTO> messageCaptor = ArgumentCaptor.forClass(MessageDTO.class);
        verify(template, times(1)).convertAndSend(destinationCaptor.capture(), messageCaptor.capture());

        // 전송된 destination과 messageDTO 검증
        assertEquals("/topic/room/" + chatRoom.getChatroomId(), destinationCaptor.getValue());
        MessageDTO capturedMessageDTO = messageCaptor.getValue();
        assertEquals(MessageType.LEAVE, capturedMessageDTO.getType());
        assertEquals(chatRoom.getChatroomId(), capturedMessageDTO.getChatRoomId());
        assertTrue(capturedMessageDTO.getContent().contains("user1"));
    }
}
