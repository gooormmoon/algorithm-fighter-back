package gooroommoon.algofi_core.chat;

import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.repository.ChatRoomRepository;
import gooroommoon.algofi_core.chat.service.ChatRoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @MockBean
    private ChatRoomRepository chatRoomRepository;

    @Test
    public void testSaveChatRoom() {
        Chatroom chatRoom = new Chatroom();
        chatRoom.setChatroomName("Test Room");

        given(chatRoomRepository.save(any(Chatroom.class))).willReturn(chatRoom);

        Chatroom savedRoom = chatRoomService.saveChatRoom("Test Room");

        assertEquals("Test Room", savedRoom.getChatroomName());
        verify(chatRoomRepository, times(1)).save(any(Chatroom.class));
    }

    @Test
    public void testFindRoomById() {
        Long roomId = 1L;
        Chatroom chatRoom = new Chatroom();
        chatRoom.setChatroomId(roomId);
        chatRoom.setChatroomName("Test Room");

        given(chatRoomRepository.findById(roomId)).willReturn(Optional.of(chatRoom));

        Chatroom foundRoom = chatRoomService.findRoomById(roomId);

        assertEquals("Test Room", foundRoom.getChatroomName());
    }
}

