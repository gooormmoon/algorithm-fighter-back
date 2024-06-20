package gooroommoon.algofi_core.gameresult;

import gooroommoon.algofi_core.gameresult.dto.GameresultResponse;
import gooroommoon.algofi_core.gameresult.dto.GameresultsResponse;
import gooroommoon.algofi_core.gameresult.dto.GameresultsResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameresultController {

    private final GameresultService gameresultService;

    /**
     * 멤버의 특정 게임결과 조회
     */
    //TODO uri 정해야함
    @GetMapping("/api/game/member/{GameresultId}")
    public ResponseEntity findGameresult(@PathVariable Long GameresultId, Principal principal) {
        GameresultResponse gameresult = gameresultService.findGameresult(principal.getName(), GameresultId);

        //TODO 상태코드랑, 메세지같이 보내야함
        return ResponseEntity.ok().body(gameresult);
    }

    /**
     * 멤버의 모든 게임결과 조회
     */
    //TODO uri 정해야함
    @GetMapping("/api/game/member/gameresults")
    public ResponseEntity<GameresultsResponseWrapper> findAllGameresults(Principal principal) {
        List<GameresultsResponse> gameresultList = gameresultService.findGameresultList(principal.getName());

        GameresultsResponseWrapper responseWrapper = GameresultsResponseWrapper.builder()
                .statusCode(HttpStatus.OK.value())
                .message("성공")
                .gameresultList(gameresultList)
                .build();

        return ResponseEntity.ok().body(responseWrapper);
    }
}
