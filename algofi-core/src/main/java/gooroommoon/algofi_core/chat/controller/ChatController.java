package gooroommoon.algofi_core.chat.controller;

import gooroommoon.algofi_core.chat.dto.MessageDTO;
import gooroommoon.algofi_core.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/enter-room/{roomId}")
    @SendTo("/topic/room/{roomId}")
    public void enterRoom(@PathVariable Long roomId, @Payload MessageDTO message, SimpMessageHeaderAccessor headerAccessor) {
        chatService.enterRoom(roomId, message, headerAccessor);
    }
    @MessageMapping("/send-message")
    public void sendMessage(@Payload MessageDTO message) {
        chatService.saveAndSendMessage(message);
    }

    @GetMapping("/{chatRoomId}/messages")
    public List<MessageDTO> getMessagesInChattingRoom(@PathVariable Long chatRoomId) {
        return chatService.getMessagesInChattingRoom(chatRoomId);
    }

}
