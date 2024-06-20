package gooroommoon.algofi_core.algorithmproblem.testcase;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Testcase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String testInput;

    @NotNull
    private String testOutput;

    @Column(name = "algorithm_problem_id", nullable = false)
    private Long algorithmProblemId;
}
