package gooroommoon.algofi_core.game.message;

import gooroommoon.algofi_core.game.message.successconst.SuccessCode;
import gooroommoon.algofi_core.game.message.successconst.SuccessMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendSuccessDelete{

    private String code;
    private String message;
    private boolean deleteStatus;

    public SendSuccessDelete(String code, String message, boolean deleteStatus) {
        this.code =code;
        this.message = message;
        this.deleteStatus = deleteStatus;
    }

    public static SendSuccessDelete of(String code, String message, boolean deleteStatus) {
        return new SendSuccessDelete(code, message, deleteStatus);
    }

    public static SendSuccessDelete of(boolean deleteStatus) {
        return new SendSuccessDelete(SuccessCode.OK, SuccessMessage.OK, deleteStatus);
    }
}
