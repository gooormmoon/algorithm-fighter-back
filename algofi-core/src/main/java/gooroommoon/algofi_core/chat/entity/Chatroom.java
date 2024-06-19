package gooroommoon.algofi_core.chat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter @Setter
@AllArgsConstructor
public class Chatroom {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "CHATROOM_ID", updatable = false, nullable = false)
    private UUID chatroomId;

    private String chatroomName;

    public Chatroom() {}

    public Chatroom(String roomName) {
        this.chatroomName = roomName;
    }
}