package gooroommoon.algofi_core.chat.service;

import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.repository.ChatroomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ChatroomServiceTest {

    @Autowired
    private ChatroomService chatroomService;

    @MockBean
    private ChatroomRepository chatRoomRepository;

    @Test
    @DisplayName("채팅방 ID로 조회 테스트")
    public void testFindRoomById() {
        // 가짜 채팅방 UUID
        String roomId = UUID.randomUUID().toString();

        // 가짜 채팅방 객체 생성
        Chatroom mockChatroom = new Chatroom();
        mockChatroom.setChatroomId(roomId);

        // chatRoomRepository.findByChatroomId() 메서드가 호출되었을 때 반환할 가짜 객체 설정
        when(chatRoomRepository.findByChatroomId(roomId)).thenReturn(Optional.of(mockChatroom));

        // 테스트할 메서드 호출
        Chatroom foundRoom = chatroomService.findRoomById(roomId);

        // 조회된 채팅방이 예상대로 반환되는지 검증
        assertNotNull(foundRoom);
        assertEquals(roomId, foundRoom.getChatroomId());

        // findByChatroomId() 메서드가 한 번 호출되었는지 검증
        verify(chatRoomRepository, times(1)).findByChatroomId(roomId);
    }
}

