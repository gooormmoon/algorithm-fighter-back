package gooroommoon.algofi_core.game.gamesession;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteGameRoomDTO {

    private String gameCode;
}
