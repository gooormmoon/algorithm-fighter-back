package gooroommoon.algofi_core.chat.service;

import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.auth.member.MemberRepository;
import gooroommoon.algofi_core.chat.dto.MessageDTO;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.entity.Message;
import gooroommoon.algofi_core.chat.entity.MessageType;
import gooroommoon.algofi_core.chat.repository.ChatRoomRepository;
import gooroommoon.algofi_core.chat.repository.MessageRepository;
import gooroommoon.algofi_core.chat.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
public class ChatServiceTest {
    @InjectMocks
    private ChatService chatService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private SimpMessageSendingOperations template;

    @Test
    @DisplayName("메시지 저장하기")
    public void testSaveMessage() {
        // 가짜 인증 객체 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken("testUser", "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 가짜 멤버 생성
        Member mockMember = new Member();
        mockMember.setNickname("TestUser");
        mockMember.setLoginId("testUser");
        when(memberRepository.findByLoginId(anyString())).thenReturn(Optional.of(mockMember));

        // 가짜 채팅방 생성
        Chatroom mockChatroom = new Chatroom();
        String roomId = UUID.randomUUID().toString();
        mockChatroom.setChatroomId(roomId);
        when(chatRoomRepository.findByChatroomId(roomId)).thenReturn(Optional.of(mockChatroom));

        // 가짜 메시지 DTO 생성
        MessageDTO messageDTO = MessageDTO.builder()
                .type(MessageType.TALK)
                .chatroomId(roomId)
                .content("Hello, world!")
                .build();

        // 테스트할 메서드 호출
        chatService.saveMessage(messageDTO, authentication);

        // 메시지가 저장되었는지 확인
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("메시지 송수신")
    public void testSendMessage() {
        // 가짜 메시지 DTO 생성
        MessageDTO messageDTO = new MessageDTO();
        String roomId = UUID.randomUUID().toString();
        messageDTO.setChatroomId(roomId);
        messageDTO.setContent("Hello, world!");

        // sendMessage 호출
        chatService.sendMessage(messageDTO);

        // template.convertAndSend() 메서드가 한 번 호출되었는지 검증
        verify(template, times(1)).convertAndSend("/topic/room/" + roomId, messageDTO);
    }
}
