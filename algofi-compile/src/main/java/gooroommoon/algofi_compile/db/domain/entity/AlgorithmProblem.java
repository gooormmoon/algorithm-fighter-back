package gooroommoon.algofi_compile.db.domain.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Table(name = "algorithmproblem")
public class AlgorithmProblem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ALGORITHMPROBLEM_ID")
    private Long id;

    private String title;

    private String level;

    private String content;

    private Integer recommend_time;

    public AlgorithmProblem(Long id, String title, String level, String content, Integer recommend_time) {
        this.id = id;
        this.title = title;
        this.level = level;
        this.content = content;
        this.recommend_time = recommend_time;
    }
}
