package gooroommoon.algofi_core.chat.dto;

import gooroommoon.algofi_core.chat.entity.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class MessageDTO {

    private MessageType type;
    private Long messageId;
    private Long chatRoomId;
    private String content;
    private Long senderId;
    private LocalDateTime createdDate;

    // 엔티티에서 DTO로 변환하는 생성자
    @Builder
    public MessageDTO(MessageType type, Long messageId, Long chatRoomId, Long senderId, String content, LocalDateTime createdDate) {
        this.type = type;
        this.messageId = messageId;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.content = content;
        this.createdDate = createdDate;
    }
}
