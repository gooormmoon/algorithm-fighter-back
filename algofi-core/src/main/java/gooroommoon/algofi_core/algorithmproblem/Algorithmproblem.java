package gooroommoon.algofi_core.algorithmproblem;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Algorithmproblem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long AlgorithmproblemId;
    private String title;
    private String level;
    private String content;
    private int recommend_time;

    public Algorithmproblem(String title, String level, String content, int recommend_time) {
        this.title = title;
        this.level = level;
        this.content = content;
        this.recommend_time = recommend_time;
    }
}
