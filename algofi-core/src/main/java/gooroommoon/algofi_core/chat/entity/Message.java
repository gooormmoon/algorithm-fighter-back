package gooroommoon.algofi_core.chat.entity;


import gooroommoon.algofi_core.auth.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@NoArgsConstructor(access = PROTECTED)
@Getter @Setter
public class Message {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroomId;

    private MessageType type;

    private String content;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member senderId;


    @Column(nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Builder
    public Message(Chatroom chatroomId, Member senderId, MessageType type, String content, LocalDateTime createdDate) {
        this.chatroomId = chatroomId;
        this.senderId = senderId;
        this.type = type;
        this.content = content;
        this.createdDate = createdDate;
    }
}