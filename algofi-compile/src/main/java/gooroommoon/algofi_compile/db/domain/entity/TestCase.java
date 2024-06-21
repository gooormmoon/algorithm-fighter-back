package gooroommoon.algofi_compile.db.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "testcase")
public class TestCase {
    @Id
    @GeneratedValue
    @Column(name = "TEST_CASE_ID")
    private Long id;

    @Getter
    private String testInput;

    @Getter
    private String testOutput;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "algorithm_problem_id")
    private AlgorithmProblem algorithmProblem;
}
