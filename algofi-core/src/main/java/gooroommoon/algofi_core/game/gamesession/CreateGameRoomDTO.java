package gooroommoon.algofi_core.game.gamesession;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class CreateGameRoomDTO {

    //방장 이름
    private String hostName;
    //게임 제한인원
    private int limitMembers;

    public CreateGameRoomDTO(String hostName, int limitMembers) {
        this.hostName = hostName;
        this.limitMembers = limitMembers;
    }
}
