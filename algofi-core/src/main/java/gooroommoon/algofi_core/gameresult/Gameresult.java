package gooroommoon.algofi_core.gameresult;

import gooroommoon.algofi_core.algorithmproblem.Algorithmproblem;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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
    private Long gameresultId;

    private int runningTime;

    @NotNull
    private String hostCodeContent;

    @NotNull
    private String guestCodeContent;

    //DB table에 더 들어가야함
    @Setter
    private String hostId;

    @Setter
    private String guestId;

    private String hostCodeLanguage;

    private String guestCodeLanguage;

    private String gameOverType;

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

    public Gameresult(int runningTime, String hostCodeContent, String guestCodeContent, String hostId, String guestId, String hostCodeLanguage, String guestCodeLanguage, String gameOverType, Algorithmproblem algorithmproblemId, Chatroom chatroomId) {
        this.runningTime = runningTime;
        this.hostCodeContent = hostCodeContent;
        this.guestCodeContent = guestCodeContent;
        this.hostId = hostId;
        this.guestId = guestId;
        this.hostCodeLanguage = hostCodeLanguage;
        this.guestCodeLanguage = guestCodeLanguage;
        this.gameOverType = gameOverType;
        this.algorithmproblemId = algorithmproblemId;
        this.chatroomId = chatroomId;
    }
}
