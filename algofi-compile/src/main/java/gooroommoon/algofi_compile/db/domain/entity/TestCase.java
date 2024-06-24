package gooroommoon.algofi_compile.db.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
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
    @JoinColumn(name = "algorithmproblem_id")
    private AlgorithmProblem algorithmProblem;

    public TestCase(Long id, String testInput, String testOutput, AlgorithmProblem algorithmProblem) {
        this.id = id;
        this.testInput = testInput;
        this.testOutput = testOutput;
        this.algorithmProblem = algorithmProblem;
    }
}
