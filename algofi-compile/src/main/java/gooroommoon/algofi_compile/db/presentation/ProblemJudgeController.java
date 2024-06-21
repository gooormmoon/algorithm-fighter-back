package gooroommoon.algofi_compile.db.presentation;

import gooroommoon.algofi_compile.db.application.ProblemJudgeService;
import gooroommoon.algofi_compile.db.presentation.dto.ProblemJudgeRequest;
import gooroommoon.algofi_compile.db.presentation.dto.ProblemJudgeResponse;
import gooroommoon.algofi_compile.judge.exception.CodeExecutionException;
import gooroommoon.algofi_compile.judge.exception.RequestException;
import gooroommoon.algofi_compile.judge.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProblemJudgeController {

    private final ProblemJudgeService problemJudgeService;

    @PostMapping("/api/judge-problem")
    public ResponseEntity<ProblemJudgeResponse> judgeProblem(@RequestBody ProblemJudgeRequest request) {
        ProblemJudgeResponse result = problemJudgeService.judgeProblem(request.getLanguage(), request.getAlgorithmProblemId(), request.getCode());
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler(value = {ServerException.class})
    public ResponseEntity<ProblemJudgeResponse> handleServerException(ServerException e) {
        ProblemJudgeResponse response = new ProblemJudgeResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {RequestException.class})
    public ResponseEntity<ProblemJudgeResponse> handleRequestException(RequestException e) {
        ProblemJudgeResponse response = new ProblemJudgeResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {CodeExecutionException.class})
    public ResponseEntity<ProblemJudgeResponse> handleCodeExecutionException(CodeExecutionException e) {
        ProblemJudgeResponse response = new ProblemJudgeResponse(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
