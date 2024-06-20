package gooroommoon.algofi_core.algorithmproblem;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlgorithmProblem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long algorithmProblemId;
    private String title;

    @JsonProperty("level")
    private String level;
    private String content;
    private int recommend_time;

}
