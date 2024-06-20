package gooroommoon.algofi_core.gameresult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameResultRequest {
    private String runningTime;
    private String hostCodeContent;
    private String guestCodeContent;
    private Long algorithmProblemId;
    private Long chatRoomId;
}
