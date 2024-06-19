package gooroommoon.algofi_core.game.session.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GameSessionResponse {
    private String title;

    private String host;

    private Set<String> players;

    private Set<String> readyPlayers;

    private int maxPlayer;

    private String problemLevel;

    private int timerTime;
}
