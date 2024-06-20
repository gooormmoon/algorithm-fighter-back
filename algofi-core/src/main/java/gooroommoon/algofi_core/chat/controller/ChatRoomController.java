package gooroommoon.algofi_core.chat.controller;

import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/api/chat/list")
    public ResponseEntity findAllChatroom() {
        List<Chatroom> allChatroom = chatRoomService.findAllChatroom();

        return ResponseEntity.ok().body(allChatroom);
    }
}
