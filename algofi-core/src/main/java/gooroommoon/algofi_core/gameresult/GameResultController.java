package gooroommoon.algofi_core.gameresult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameResultController {

    private final GameResultService gameResultService;

    /**
     * response 데이터에서 게임결과와 관련된 데이터를 갖고 gameResult에 저장
     */
    @MessageMapping("/game/save")
    @SendTo("/topic/game")
    public GameResult saveGameResult(String gameResult, Authentication auth) {
        return gameResultService.save(gameResult, auth.getName());
    }

    /**
     * 멤버의 특정 게임결과 조회 , member 정보를 세션에서 가져와야함
     */
    @GetMapping("/app/game/member/{gameResultId}")
    public ResponseEntity findGameResult(@PathVariable Long gameResultId,Authentication auth) {
        GameResult gameResult = gameResultService.findGameResult(auth.getName(), gameResultId);

        //상태코드랑, 메세지같이 보내야함
        return ResponseEntity.ok().body(gameResult);
    }

    /**
     * 멤버의 모든 게임결과 조회 , member 정보를 세션에서 가져와야함
     */
    @GetMapping("/app/game/member/gameResults")
    public ResponseEntity findAllGameResults(Authentication auth) {
        List<GameResult> gameResultList = gameResultService.findGameResultList(auth.getName());

        //상태코드랑, 메세지같이 보내야함
        return ResponseEntity.ok().body(gameResultList);
    }
}
