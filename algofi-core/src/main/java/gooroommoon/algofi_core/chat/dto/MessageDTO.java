package gooroommoon.algofi_core.chat.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gooroommoon.algofi_core.chat.entity.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MessageDTO {

    private MessageType type;
    private Long messageId;
    private UUID chatRoomId;
    private String content;
    private String senderId;
    private LocalDateTime createdDate;

    // 엔티티에서 DTO로 변환하는 생성자
    @Builder
    public MessageDTO(MessageType type, Long messageId, UUID chatRoomId, String senderId, String content, LocalDateTime createdDate) {
        this.type = type;
        this.messageId = messageId;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.content = content;
        this.createdDate = createdDate;
    }
}
