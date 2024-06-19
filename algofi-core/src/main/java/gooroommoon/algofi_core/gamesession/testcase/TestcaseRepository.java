package gooroommoon.algofi_core.gamesession.testcase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TestcaseRepository extends JpaRepository<Testcase, Long> {

    /**
     * 문제에 맞는 테스트 케이스들을 가져오는 쿼리
     */
    @Query("select t from Testcase t inner join AlgorithmProblem a on t.algorithmProblemId = a.algorithmProblemId where a.algorithmProblemId = :algorithmProblemId")
    List<Testcase> findAllByAlgorithmProblemId(@Param("algorithmProblemId")Long algorithmProblemId);
}
