package gooroommoon.algofi_core.gameresult;

import gooroommoon.algofi_core.gameresult.dto.GameResultResponse;
import gooroommoon.algofi_core.gameresult.dto.GameResultsResponse;
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
     * 멤버의 특정 게임결과 조회
     */
    //TODO uri 정해야함
    @GetMapping("/app/game/member/{gameResultId}")
    public ResponseEntity findGameResult(@PathVariable Long gameResultId,Authentication auth) {
        GameResultResponse gameResult = gameResultService.findGameResult(auth.getName(), gameResultId);

        //TODO 상태코드랑, 메세지같이 보내야함
        return ResponseEntity.ok().body(gameResult);
    }

    /**
     * 멤버의 모든 게임결과 조회
     */
    //TODO uri 정해야함
    @GetMapping("/app/game/member/gameResults")
    public ResponseEntity findAllGameResults(Authentication auth) {
        List<GameResultsResponse> gameResultList = gameResultService.findGameResultList(auth.getName());

        //TODO 상태코드랑, 메세지같이 보내야함
        return ResponseEntity.ok().body(gameResultList);
    }
}
