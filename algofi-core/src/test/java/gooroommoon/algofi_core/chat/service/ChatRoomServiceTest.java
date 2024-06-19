package gooroommoon.algofi_core.chat.service;

import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.repository.ChatRoomRepository;
import gooroommoon.algofi_core.chat.service.ChatRoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @MockBean
    private ChatRoomRepository chatRoomRepository;

    @Test
    @DisplayName("채팅방 저장하기")
    public void testSaveChatRoom() {
        // 가짜 채팅방 이름
        String roomName = "Test Room";

        // 가짜 채팅방 객체 생성
        Chatroom mockChatroom = new Chatroom(roomName);

        // chatRoomRepository.save() 메서드가 호출되었을 때 반환할 가짜 객체 설정
        when(chatRoomRepository.save(any(Chatroom.class))).thenReturn(mockChatroom);

        // 테스트할 메서드 호출
        Chatroom savedRoom = chatRoomService.saveChatRoom(roomName);

        // 저장된 채팅방이 예상대로 반환되는지 검증
        assertNotNull(savedRoom);
        assertEquals(roomName, savedRoom.getChatroomName());

        // chatRoomRepository.save() 메서드가 한 번 호출되었는지 검증
        verify(chatRoomRepository, times(1)).save(any(Chatroom.class));
    }

    @Test
    @DisplayName("채팅방 존재 여부 확인 후 반환 테스트")
    public void testEnsureChatRoomExists() {
        // 가짜 채팅방 UUID
        UUID roomId = UUID.randomUUID();

        // 존재하지 않는 채팅방을 찾을 때 반환할 가짜 객체 설정
        when(chatRoomRepository.findByChatroomId(roomId)).thenReturn(Optional.empty());

        // 새로운 채팅방을 생성할 때 반환할 가짜 객체 설정
        Chatroom mockChatroom = new Chatroom();
        mockChatroom.setChatroomId(roomId);
        when(chatRoomRepository.save(any(Chatroom.class))).thenReturn(mockChatroom);

        // 테스트할 메서드 호출
        Chatroom chatRoom = chatRoomService.ensureChatRoomExists(roomId);

        // 반환된 채팅방이 예상대로 생성되었는지 검증
        assertNotNull(chatRoom);
        assertEquals(roomId, chatRoom.getChatroomId());

        // findByChatroomId()와 save() 메서드가 각각 한 번 호출되었는지 검증
        verify(chatRoomRepository, times(1)).findByChatroomId(roomId);
        verify(chatRoomRepository, times(1)).save(any(Chatroom.class));
    }

    @Test
    @DisplayName("채팅방 ID로 조회 테스트")
    public void testFindRoomById() {
        // 가짜 채팅방 UUID
        UUID roomId = UUID.randomUUID();

        // 가짜 채팅방 객체 생성
        Chatroom mockChatroom = new Chatroom();
        mockChatroom.setChatroomId(roomId);

        // chatRoomRepository.findByChatroomId() 메서드가 호출되었을 때 반환할 가짜 객체 설정
        when(chatRoomRepository.findByChatroomId(roomId)).thenReturn(Optional.of(mockChatroom));

        // 테스트할 메서드 호출
        Chatroom foundRoom = chatRoomService.findRoomById(roomId);

        // 조회된 채팅방이 예상대로 반환되는지 검증
        assertNotNull(foundRoom);
        assertEquals(roomId, foundRoom.getChatroomId());

        // findByChatroomId() 메서드가 한 번 호출되었는지 검증
        verify(chatRoomRepository, times(1)).findByChatroomId(roomId);
    }
}

