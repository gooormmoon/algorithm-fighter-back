package gooroommoon.algofi_core.algorithmproblem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlgorithmproblemRepository extends JpaRepository<Algorithmproblem, Long> {

    /**
     *랜덤으로 문제 가져오는 쿼리
     */
    @Query("select a from Algorithmproblem a where a.level like :level order by rand() limit 1")
    Algorithmproblem getRandomAlgorithmproblem(@Param("level")String level);
}
