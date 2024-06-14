package gooroommoon.algofi_core.game.message.successconst;

import lombok.Getter;

@Getter
public class SuccessMessage {
    public static final String OK = "성공";
    public static final String OK_REQUEST_CREATE_ROOM = "방 생성에 성공하였습니다.";

    public static final String BAD_REQUEST = "실패";
    public static final String BAD_REQUEST_GUEST_MEMBER = "방장만 삭제할 수 있습니다.";
    public static final String BAD_REQUEST_ENTER_GAME = "게임에 입장할 수 없습니다.";
}
