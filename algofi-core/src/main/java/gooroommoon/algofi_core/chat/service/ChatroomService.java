package gooroommoon.algofi_core.chat.service;

import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.repository.ChatroomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatroomService {
    private final ChatroomRepository chatRoomRepository;
    @Transactional(readOnly = true)
    public Chatroom findRoomById(String roomId) {
        log.info("FindRoomById : {}", roomId);
        return chatRoomRepository.findByChatroomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다. roomId: " + roomId));
    }
}
