package gooroommoon.algofi_core.game.gamesession;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameJoinMember {

    private String hostName;

    private Long roomId;

    public GameJoinMember(String hostName, Long roomId) {
        this.hostName = hostName;
        this.roomId = roomId;
    }

    public static GameJoinMember of(String hostName, Long roomId) {
        return new GameJoinMember(hostName, roomId);
    }

    public static GameJoinMember of(GameRoom gameRoom) {
        return new GameJoinMember(gameRoom.getHostName(), gameRoom.getId());
    }
}
