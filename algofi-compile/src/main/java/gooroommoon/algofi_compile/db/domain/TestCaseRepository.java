package gooroommoon.algofi_compile.db.domain;

import gooroommoon.algofi_compile.db.domain.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    List<TestCase> findAllByAlgorithmProblemId(Long algorithmProblemId);
}
