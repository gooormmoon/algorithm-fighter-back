package gooroommoon.algofi_core.game.gamesession;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestGameRoomDTO {

    private String hostName;
    private Long roomId;
    private String gameCode;
    private int limitMembers;
}
