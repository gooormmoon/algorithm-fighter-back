package gooroommoon.algofi_core.chat.service;

import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public Chatroom saveChatRoom(String roomName) {
        // 채팅방 엔티티 생성
        Chatroom chatRoom = new Chatroom(roomName);
        // 채팅방 저장
        return chatRoomRepository.save(chatRoom);
    }

    @Transactional(readOnly = true)
    public Chatroom findRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. roomId: " + roomId));
    }
}
