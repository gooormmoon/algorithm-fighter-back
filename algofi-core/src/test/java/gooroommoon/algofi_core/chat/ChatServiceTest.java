package gooroommoon.algofi_core.chat;

import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.auth.member.MemberRepository;
import gooroommoon.algofi_core.chat.dto.MessageDTO;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.entity.Message;
import gooroommoon.algofi_core.chat.entity.MessageType;
import gooroommoon.algofi_core.chat.repository.ChatRoomRepository;
import gooroommoon.algofi_core.chat.repository.MessageRepository;
import gooroommoon.algofi_core.chat.service.ChatRoomService;
import gooroommoon.algofi_core.chat.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {
    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private SimpMessageSendingOperations template;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    @Test
    @DisplayName("메시지 저장하기")
    public void testSaveMessage() {
        // 가짜 인증 객체 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 가짜 멤버 생성
        Member mockMember = new Member();
        mockMember.setNickname("tester");
        mockMember.setLoginId("testuser");
        when(memberRepository.findByLoginId(anyString())).thenReturn(Optional.of(mockMember));

        // 가짜 채팅방 생성
        Chatroom mockChatroom = new Chatroom();
        mockChatroom.setChatroomId(1L);
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(mockChatroom));

        // 가짜 메시지 DTO 생성
        MessageDTO messageDTO = MessageDTO.builder()
                .type(MessageType.TALK)
                .chatRoomId(1L)
                .content("Hello, world!")
                .build();

        // 테스트할 메서드 호출
        chatService.saveMessage(messageDTO);

        // 메시지가 저장되었는지 확인
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("메시지 송수신")
    public void testSendMessage() {
        // 가짜 메시지 DTO 생성
        MessageDTO messageDTO = MessageDTO.builder()
                .chatRoomId(1L)
                .content("Hello, world!")
                .build();

        // sendMessage 호출
        chatService.sendMessage(messageDTO);

        // template.convertAndSend() 메서드가 한 번 호출되었는지 검증
        verify(template, times(1)).convertAndSend("/topic/room/1", messageDTO);
    }

    @Test
    @DisplayName("채팅방 입장")
    public void testEnterRoom() {
        // Mock authentication
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getName()).thenReturn("testUser");

        // Mock session attributes
        Map<String, Object> sessionAttributes = new HashMap<>();
        sessionAttributes.put("username", "testUser");
        sessionAttributes.put("roomName", "testRoom");
        when(headerAccessor.getSessionAttributes()).thenReturn(sessionAttributes);

        // Mock member repository
        Member mockMember = new Member();
        mockMember.setNickname("TestUser");
        when(memberRepository.findByLoginId("testUser")).thenReturn(Optional.of(mockMember));

        // Mock chat room repository
        when(chatRoomRepository.existsById(1L)).thenReturn(false); // Room does not exist initially
        Chatroom mockChatRoom = new Chatroom();
        mockChatRoom.setChatroomId(1L);
        when(chatRoomRepository.save(any(Chatroom.class))).thenReturn(mockChatRoom);
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(mockChatRoom));

        // Mock chat room service
        when(chatRoomService.saveChatRoom("testRoom")).thenReturn(mockChatRoom);

        // Prepare message DTO
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setType(MessageType.ENTER);
        messageDTO.setChatRoomId(1L);
        messageDTO.setContent(mockMember.getNickname() + "님이 입장하셨습니다.");

        // Call the method under test
        chatService.enterRoom(1L, messageDTO, headerAccessor);

        // Verify interactions
        // enterRoom 메소드와 saveMessage 메소드에서 findByLoginId는 각각 2번 호출됨.
        verify(memberRepository, times(2)).findByLoginId("testUser");
        verify(chatRoomRepository, times(1)).existsById(1L);
        verify(chatRoomRepository, times(1)).save(any(Chatroom.class));
        verify(chatRoomService, times(1)).saveChatRoom("testRoom");
        verify(messageRepository, times(1)).save(any(Message.class));

        // Assert
        assertEquals(1L, messageDTO.getChatRoomId());
        assertEquals(MessageType.ENTER, messageDTO.getType());
        assertEquals("TestUser님이 입장하셨습니다.", messageDTO.getContent());
    }
}
