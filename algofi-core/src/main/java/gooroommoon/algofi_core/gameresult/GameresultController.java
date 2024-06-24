package gooroommoon.algofi_core.gameresult;

import gooroommoon.algofi_core.gameresult.dto.GameresultResponse;
import gooroommoon.algofi_core.gameresult.dto.StateResponse;
import gooroommoon.algofi_core.gameresult.dto.GameresultsResponse;
import gooroommoon.algofi_core.gameresult.membergameresult.MemberGameresultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameresultController {

    private final GameresultService gameresultService;
    private final MemberGameresultRepository memberGameresultRepository;

    /**
     * 멤버의 특정 게임결과 조회
     */
    @GetMapping("/app/game/result/{gameresultId}")
    public ResponseEntity<StateResponse<GameresultResponse>> findGameresult(@PathVariable("gameresultId") Long gameresultId, Authentication auth) {
        GameresultResponse gameresult = gameresultService.findGameresult(auth.getName(), gameresultId);
        String memberGameOverType = memberGameresultRepository.findMemberGameOverType(auth.getName(), gameresultId);
        gameresult.setGameOverType(memberGameOverType);

        StateResponse<GameresultResponse> stateResponse = StateResponse.<GameresultResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("success")
                .data(gameresult)
                .build();

        return ResponseEntity.ok(stateResponse);
    }

    /**
     * 멤버의 모든 게임결과 조회
     */
    @GetMapping("/app/game/results")
    public ResponseEntity<StateResponse<List<GameresultsResponse>>> findAllGameresults(Authentication auth) {
        List<GameresultsResponse> gameresultList = gameresultService.findGameresultList(auth.getName());

        StateResponse<List<GameresultsResponse>> stateResponse = StateResponse.<List<GameresultsResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success")
                .data(gameresultList)
                .build();

        return ResponseEntity.ok(stateResponse);
    }
}
