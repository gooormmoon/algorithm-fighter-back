package gooroommoon.algofi_core.gamesession.algorithmproblem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlgorithmProblemRepository extends JpaRepository<AlgorithmProblem, Long> {

    /**
     *랜덤으로 문제 가져오는 쿼리
     */
    @Query("select a from AlgorithmProblem a where a.level like :level order by rand() limit 1")
    AlgorithmProblem getRandomAlgorithmProblem(@Param("level")String level);
}
