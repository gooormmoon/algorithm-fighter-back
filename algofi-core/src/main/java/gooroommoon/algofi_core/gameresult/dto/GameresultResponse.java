package gooroommoon.algofi_core.gameresult.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gooroommoon.algofi_core.algorithmproblem.AlgorithmProblem;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GameResultResponse {
    private int runningTime;

    private String hostCodeContent;

    private String guestCodeContent;

    private AlgorithmProblem algorithmProblemId;

    private Chatroom chatroomId;
}
