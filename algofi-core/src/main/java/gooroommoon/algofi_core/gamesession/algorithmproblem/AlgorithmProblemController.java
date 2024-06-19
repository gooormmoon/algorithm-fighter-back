package gooroommoon.algofi_core.gamesession.algorithmproblem;

import gooroommoon.algofi_core.gamesession.testcase.Testcase;
import gooroommoon.algofi_core.gamesession.testcase.TestcaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AlgorithmProblemController {

    private final AlgorithmProblemService algorithmProblemService;
    private final TestcaseService testcaseService;

    @MessageMapping("/game/problem")
    @SendTo("/topic/game")
    public AlgorithmProblem random(String level) {
        AlgorithmProblem algorithmProblem = algorithmProblemService.getRandom(level);
        return algorithmProblem;
    }

    /**
     * 테스트 케이스 전달
     */
//    @PostMapping("/app/game/{algorithmProblemId}")
//    public ResponseEntity getTestcases(@RequestBody @PathVariable Long algorithmProblemId) {
//        List<Testcase> testcases = testcaseService.getTestcases(algorithmProblemId);
//
//        return ResponseEntity.ok().body(testcases);
//    }
}
