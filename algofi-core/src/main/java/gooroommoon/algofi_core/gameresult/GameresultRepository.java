package gooroommoon.algofi_core.gameresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameresultRepository extends JpaRepository<Gameresult, Long> {

    /*
    member의 모든 게임 결과 조회
     */
    @Query("select gr from Gameresult gr inner join fetch MemberGameresult mg on gr.GameresultId = mg.gameresult.GameresultId " +
            "inner join fetch Member m on mg.member.loginId = m.loginId where m.loginId = :loginId")
    List<Gameresult> findGameresultsByMemberId(@Param("loginId")String loginId);

    /*
    member의 특정한 게임 결과 조회
     */
    @Query("select gr from Gameresult gr inner join fetch MemberGameresult mg on gr.GameresultId = mg.gameresult.GameresultId " +
            "inner join fetch Member m on mg.member.loginId = m.loginId where m.loginId = :loginId and gr.GameresultId = :gameresultId")
    Gameresult findGameresultByMemberIdAndGameresultId(@Param("loginId")String loginId, @Param("gameresultId")Long gameresultId);
}
