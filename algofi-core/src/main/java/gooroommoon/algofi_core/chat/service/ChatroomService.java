package gooroommoon.algofi_core.chat.service;

import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.repository.ChatroomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatroomService {
    private final ChatroomRepository chatRoomRepository;

    @Transactional
    public Chatroom saveChatRoom(String roomName) {
        // 채팅방 엔티티 생성
        Chatroom chatroom = new Chatroom(roomName);
        // 채팅방 저장
        return chatRoomRepository.save(chatroom);
    }

    @Transactional
    public Chatroom ensureChatRoomExists(String roomId) {
        return chatRoomRepository.findByChatroomId(roomId)
                .orElseGet(() -> {
                    Chatroom chatroom = new Chatroom();
                    chatroom.setChatroomId(roomId);
                    return chatRoomRepository.save(chatroom);
                });
    }

    @Transactional(readOnly = true)
    public Chatroom findRoomById(String roomId) {
        System.out.println("ROOMID : "+roomId);
        return chatRoomRepository.findByChatroomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. roomId: " + roomId));
    }
}
