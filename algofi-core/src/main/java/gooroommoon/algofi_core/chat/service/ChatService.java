package gooroommoon.algofi_core.chat.service;

import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.auth.member.MemberRepository;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.entity.Message;
import gooroommoon.algofi_core.chat.entity.MessageType;
import gooroommoon.algofi_core.chat.dto.MessageDTO;
import gooroommoon.algofi_core.chat.repository.ChatRoomRepository;
import gooroommoon.algofi_core.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final SimpMessageSendingOperations template;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomService chatRoomService;

    @Transactional
    public void saveMessage(MessageDTO messageDTO, Principal principal) {

        // 인증 정보에서 사용자 이름(로그인 ID) 가져오기
        String loginId = principal.getName();

        // 로그인 ID를 기반으로 Member 엔티티 조회
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("현재 인증된 사용자의 정보를 찾을 수 없습니다."));

        // 채팅방 찾기
        Chatroom chatroom = chatRoomRepository.findByChatroomId(messageDTO.getChatRoomId())
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // Message 엔티티 생성 및 저장
        Message message = Message.builder()
                .chatroomId(chatroom)
                .senderId(member)
                .type(messageDTO.getType())
                .content(messageDTO.getContent())
                .createdDate(LocalDateTime.now())
                .build();

        log.info("접속 채팅방 ID: {}", message.getChatroomId().getChatroomId());
        log.info("Content: {}", message.getContent());
        messageRepository.save(message);
    }

    public void sendMessage(MessageDTO message) {
        log.info("접속 채팅방 ID: {}", message.getChatRoomId());
        log.info("접속 채팅방 Content: {}", message.getContent());
        template.convertAndSend("/topic/room/" + message.getChatRoomId(), message);
    }

    // WebSocket 연결이 끊겼을 때
    @EventListener
    @Transactional
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Principal principal = headerAccessor.getUser();
        // principal null check
        if (principal == null) {
            throw new IllegalArgumentException("Principal must not be null");
        }

        String username = principal.getName();

        // 사용자가 참여한 채팅방 조회
        Optional<Chatroom> optionalChatRoom = chatRoomRepository.findByChatroomName(username);
        Chatroom chatRoom = optionalChatRoom
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found."));

        // chat room 삭제
        chatRoomRepository.delete(chatRoom);

        log.info("chatRoom: {}", chatRoom);

        // 퇴장 메시지 생성 및 전송
        String leaveMessage = username + "님이 퇴장하셨습니다.";
        MessageDTO message = MessageDTO.builder()
                .type(MessageType.LEAVE)
                .chatRoomId(chatRoom.getChatroomId())
                .content(leaveMessage)
                .build();
        template.convertAndSend("/topic/room/" + chatRoom.getChatroomId(), message);
    }

    @Transactional
    public List<MessageDTO> getMessagesInChattingRoom(UUID chatRoomId) {

        // 채팅방의 메시지 목록을 가져옴
        List<Message> messages = messageRepository.findByChatroomIdChatroomId(chatRoomId);

        // Message 엔티티를 DTO로 반환
        return messages.stream()
                .map(message -> MessageDTO.builder()
                        .type(message.getType())
                        .messageId(message.getId())
                        .chatRoomId(message.getChatroomId().getChatroomId())
                        .content(message.getContent())
                        .senderId(message.getSenderId().getId())
                        .createdDate(message.getCreatedDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveAndSendMessage(MessageDTO message, Principal principal) {
        saveMessage(message, principal);
        sendMessage(message);
    }

    @Transactional
    public void enterRoom(UUID roomId, MessageDTO message, Principal principal) {
        log.info("방 ID: {}로 입장", roomId);

        // 현재 인증된 사용자 정보 가져오기
        String username = principal.getName();
        Member member = memberRepository.findByLoginId(username)
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        // 채팅방이 존재하는지 확인하고 없으면 생성하기
        Chatroom chatRoom = chatRoomService.ensureChatRoomExists(roomId);
        chatRoom.setChatroomId(roomId);
        chatRoom.setChatroomName(username);
        chatRoomRepository.save(chatRoom);

        message.setChatRoomId(roomId);
        message.setType(MessageType.ENTER);
        message.setContent(member.getNickname() + "님이 입장하셨습니다.");

        saveMessage(message, principal);
    }
}
