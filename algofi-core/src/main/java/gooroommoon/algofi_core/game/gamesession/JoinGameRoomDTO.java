package gooroommoon.algofi_core.game.gamesession;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinGameRoomDTO {

    private String hostName;
    private String gameCode;
}
