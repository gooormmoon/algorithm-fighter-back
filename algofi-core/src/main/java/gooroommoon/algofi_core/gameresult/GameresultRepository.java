package hello.proxy.gameresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {

    /*
    member의 모든 게임 결과 조회
     */
    @Query("select gr from GameResult gr inner join fetch MemberGameResult mg on gr.id = mg.gameResult.id " +
            "inner join fetch Member m on mg.member.id = m.id where m.loginId = :loginId")
    List<GameResult> findGameResultsByMemberId(@Param("loginId")String loginId);

    /*
    member의 특정한 게임 결과 조회
     */
    @Query("select gr from GameResult gr inner join fetch MemberGameResult mg on gr.id = mg.gameResult.id " +
            "inner join fetch Member m on mg.member.id = m.id where m.loginId = :loginId and gr.id = :gameResultId")
    GameResult findGameResultByMemberIdAndGameResultId(@Param("loginId")String loginId, @Param("gameResultId")Long gameResultId);
}
