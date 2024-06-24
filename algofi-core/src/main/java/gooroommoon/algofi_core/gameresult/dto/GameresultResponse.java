package gooroommoon.algofi_core.gameresult.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gooroommoon.algofi_core.algorithmproblem.Algorithmproblem;
import gooroommoon.algofi_core.game.session.GameOverType;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GameresultResponse {
    private int runningTime;

    private String hostCodeContent;

    private String guestCodeContent;

    private String title;
    
    private String hostId;
    
    private String guestId;

    @Setter
    private String gameOverType;
    
    private String hostCodeLanguage;
    
    private String guestCodeLanguage;
}
