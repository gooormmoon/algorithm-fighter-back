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
    @Column(name = "CHATROOM_ID", updatable = false, nullable = false)
    private String chatroomId;

    private String chatroomName;

    public Chatroom() {}

    public Chatroom(String roomName) {
        this.chatroomName = roomName;
    }
}