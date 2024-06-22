package gooroommoon.algofi_compile.db.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "algorithmproblem")
public class AlgorithmProblem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ALGORITHM_PROBLEM_ID")
    private Long id;

    private String title;

    private String level;

    private String content;

    private Integer recommend_time;

}
