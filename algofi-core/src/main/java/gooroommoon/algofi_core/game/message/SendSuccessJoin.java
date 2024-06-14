package gooroommoon.algofi_core.game.message;

import gooroommoon.algofi_core.game.message.successconst.SuccessCode;
import gooroommoon.algofi_core.game.message.successconst.SuccessMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendSuccessJoin extends SendSuccess{

    private boolean joinStatus;

    public SendSuccessJoin(String code, String message, boolean joinStatus) {
        super(code, message);
        this.joinStatus = joinStatus;
    }

    public static SendSuccessJoin of(String code, String message, boolean joinStatus) {
        return new SendSuccessJoin(code, message, joinStatus);
    }

    public static SendSuccessJoin of(boolean joinStatus) {
        if(joinStatus){
            return new SendSuccessJoin(SuccessCode.OK, SuccessMessage.OK, true);
        }else {
            return new SendSuccessJoin(SuccessCode.BAD_REQUEST, SuccessMessage.BAD_REQUEST_ENTER_GAME, false);
        }
    }
}
