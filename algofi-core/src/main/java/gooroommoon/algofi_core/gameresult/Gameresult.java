package gooroommoon.algofi_core.gameresult;

import gooroommoon.algofi_core.algorithmproblem.Algorithmproblem;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Gameresult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long GameresultId;

    private int runningTime;

    @NotNull
    private String hostCodeContent;

    @NotNull
    private String guestCodeContent;

    @CreatedDate
    @NotNull
    @Setter
    private LocalDateTime created_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algorithm_problem_id", nullable = false)
    private Algorithmproblem algorithmproblemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroomId;

    public Gameresult(int runningTime, String hostCodeContent, String guestCodeContent, Algorithmproblem algorithmproblemId, Chatroom chatroomId) {
        this.runningTime = runningTime;
        this.hostCodeContent = hostCodeContent;
        this.guestCodeContent = guestCodeContent;
        this.algorithmproblemId = algorithmproblemId;
        this.chatroomId = chatroomId;
    }
}
