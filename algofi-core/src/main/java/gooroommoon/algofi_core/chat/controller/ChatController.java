package gooroommoon.algofi_core.chat.controller;

import gooroommoon.algofi_core.chat.dto.MessageDTO;
import gooroommoon.algofi_core.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    @MessageMapping("/enter-room/{roomId}")
    public void enterMessage(@DestinationVariable String roomId, Principal principal) {
        System.out.println("Room ID: " + roomId);
        chatService.enterRoom(roomId, principal.getName());
    }
    @MessageMapping("/send-message")
    public void sendMessage(@Payload MessageDTO message, Principal principal) {
        chatService.saveAndSendMessage(message, principal);
    }

    @GetMapping("/chat/{roomId}/messages")
    public List<MessageDTO> getMessagesInChattingRoom(@PathVariable String roomId) {
        return chatService.getMessagesInChattingRoom(roomId);
    }
}
