package gooroommoon.algofi_core.gameresult.membergameresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberGameresultRepository extends JpaRepository<MemberGameresult, Long> {

    @Query("select mg.gameOverType from MemberGameresult mg " +
            "inner join mg.member m " +
            "inner join mg.gameresult g " +
            "where m.loginId = :loginId and g.gameresultId = :gameresultId")
    String findMemberGameOverType(@Param("loginId") String loginId, @Param("gameresultId") Long gameresultId);
}
