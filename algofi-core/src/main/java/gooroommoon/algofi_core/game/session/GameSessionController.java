package gooroommoon.algofi_core.game.session;

import gooroommoon.algofi_core.dto.ExceptionResponse;
import gooroommoon.algofi_core.game.session.dto.GameSessionJoinRequest;
import gooroommoon.algofi_core.game.session.dto.GameSessionUpdateRequest;
import gooroommoon.algofi_core.game.session.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@MessageMapping("/game")
@Controller
@RequiredArgsConstructor
public class GameSessionController {

    private final GameSessionService gameSessionService;

    @MessageMapping("/create")
    public void createGame(Principal principal, @Payload GameSessionUpdateRequest request) {
        gameSessionService.createSession(principal.getName(), request);
    }

    @MessageMapping("/join")
    public void joinGame(Principal principal, @Payload GameSessionJoinRequest request) {
        gameSessionService.addPlayer(request.getHostId(), principal.getName());
    }

    @MessageMapping("/updates")
    public void updateSettings(Principal principal, @Payload GameSessionUpdateRequest request) {
        gameSessionService.updateSetting(principal.getName(), request);
    }

    @MessageMapping("/ready")
    public void ready(Principal principal) {
        gameSessionService.playerReady(principal.getName());
    }

    @MessageMapping("/start")
    public void startGame(Principal principal) {
        gameSessionService.startGame(principal.getName());
    }

    @MessageMapping("/submit")
    public void submitCode(Principal principal) {
        gameSessionService.submitCode(principal.getName());
    }

    @MessageExceptionHandler({
            AlreadyInGameSessionException.class,
            GameIsFullException.class,
            GameSessionNotFoundException.class,
            NotAHostException.class,
            PlayersNotReadyException.class})
    @SendToUser("/queue/game/session")
    public ExceptionResponse handleException(Exception exception) {
        return new ExceptionResponse(exception.getMessage());
    }


}
