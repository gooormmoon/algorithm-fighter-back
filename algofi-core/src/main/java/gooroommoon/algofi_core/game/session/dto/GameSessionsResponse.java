package gooroommoon.algofi_core.game.session.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GameSessionsResponse {

    private List<Session> rooms;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Session {
        private String host;
        private String hostId;
        private String title;
        private int maxPlayer;
        private String problemLevel;
        private int timerTime;
        private boolean isStarted;
    }
}
