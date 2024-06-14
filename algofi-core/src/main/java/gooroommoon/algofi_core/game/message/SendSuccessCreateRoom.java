package gooroommoon.algofi_core.game.message;

import gooroommoon.algofi_core.game.message.successconst.SuccessCode;
import gooroommoon.algofi_core.game.message.successconst.SuccessMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendSuccessCreateRoom<T>{

    public String code;
    public String message;
    public T gameCode;

    public SendSuccessCreateRoom(String code, String message,T gameCode) {
        this.code = code;
        this.message = message;
        this.gameCode = gameCode;
    }

    public static <T> SendSuccessCreateRoom of(String code, String message,T gameCode) {
        return new SendSuccessCreateRoom(code, message,gameCode);
    }

    public static <T> SendSuccessCreateRoom of(T gameCode) {
        return new SendSuccessCreateRoom(SuccessCode.OK, SuccessMessage.OK_REQUEST_CREATE_ROOM,gameCode);
    }
}
