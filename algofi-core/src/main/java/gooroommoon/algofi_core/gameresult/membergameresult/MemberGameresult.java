package gooroommoon.algofi_core.gameresult.membergameresult;

import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.gameresult.Gameresult;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberGameresult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberGameresultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_result_id")
    private Gameresult gameresult;

    public MemberGameresult(Member member, Gameresult gameresult) {
        this.member = member;
        this.gameresult = gameresult;
    }
}
