package gooroommoon.algofi_core.game.gamesession;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

//json형식으로 파라미터를 받기위한 객체
@Data
public class GameChatMessage {

    @JsonProperty("type")
    private String type;

    @JsonProperty("gameRoomId")
    private Long gameRoomId;

    @JsonProperty("gameCode")
    private String gameCode;

    @JsonProperty("message")
    private String message;
}
