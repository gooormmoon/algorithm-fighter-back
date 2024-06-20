package gooroommoon.algofi_core.chat.controller;

import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/api/chat/list")
    public ResponseEntity<List<Chatroom>> findAllChatroom() {
        List<Chatroom> allChatroom = chatRoomService.findAllChatroom();

        return ResponseEntity.ok().body(allChatroom);
    }
}
