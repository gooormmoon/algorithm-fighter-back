package hello.proxy.gameresult;

import gooroommoon.algofi_core.algorithmproblem.AlgorithmProblem;
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
public class GameResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private AlgorithmProblem algorithmProblemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroomId;
}
