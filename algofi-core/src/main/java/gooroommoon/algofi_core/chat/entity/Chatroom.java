package gooroommoon.algofi_core.chat.entity;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter @Setter
public class Chatroom {

    @Id @GeneratedValue(strategy = IDENTITY)
    private Long chatroomId;

    private String chatroomName;

    public Chatroom() {}

    public Chatroom(String roomName) {
        this.chatroomName = roomName;
    }
}