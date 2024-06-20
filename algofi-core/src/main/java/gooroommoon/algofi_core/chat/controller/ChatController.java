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
    // TODO 글로벌 채팅방 id는 UUID 값으로 클라이언트가 정하기 추후 문제는 그때가서 해결
    // TODO 필요 없으면 지우기
    @MessageMapping("/enter-room/{roomId}")
    public void enterMessage(@DestinationVariable String roomId, Principal principal) {
        System.out.println("Room ID: " + roomId);
        UUID chatRoomId = UUID.fromString(roomId);
        chatService.enterRoom(chatRoomId, principal.getName());
    }
    @MessageMapping("/send-message")
    public void sendMessage(@Payload MessageDTO message, Principal principal) {
        chatService.saveAndSendMessage(message, principal);
    }

    @GetMapping("/chat/{roomId}/messages")
    public List<MessageDTO> getMessagesInChattingRoom(@PathVariable UUID roomId) {
        return chatService.getMessagesInChattingRoom(roomId);
    }
}
