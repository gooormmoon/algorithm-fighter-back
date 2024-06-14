package gooroommoon.algofi_core.game.message;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatMessageResponse<T> {

    private Long memberId;
    private String chatMessage;
    private LocalDateTime createdAt;
    private T body;

    public ChatMessageResponse(Long memberId, String chatMessage) {
        this.memberId = memberId;
        this.chatMessage = chatMessage;
        this.createdAt = LocalDateTime.now();
    }

    public ChatMessageResponse(Long memberId, String chatMessage, T body) {
        this.memberId = memberId;
        this.chatMessage = chatMessage;
        this.createdAt = LocalDateTime.now();
        this.body = body;
    }

    public static ChatMessageResponse of(ChatMessage msg) {
        return new ChatMessageResponse(msg.getMemberId(), msg.getChatMessage());
    }

    public static <T> ChatMessageResponse of(ChatMessage msg, T body) {
        return new ChatMessageResponse(msg.getMemberId(), msg.getChatMessage(), body);
    }
}
