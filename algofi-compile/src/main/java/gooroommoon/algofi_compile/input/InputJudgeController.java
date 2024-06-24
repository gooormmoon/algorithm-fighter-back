package gooroommoon.algofi_compile.input;

import gooroommoon.algofi_compile.input.dto.CodeExecutionRequest;
import gooroommoon.algofi_compile.input.dto.CodeExecutionResponse;
import gooroommoon.algofi_compile.judge.exception.CodeExecutionException;
import gooroommoon.algofi_compile.judge.exception.RequestException;
import gooroommoon.algofi_compile.judge.exception.ServerException;
import gooroommoon.algofi_compile.judge.service.JudgeResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/judge-input")
public class InputJudgeController {

    private final InputJudgeService inputJudgeService;
    @PostMapping
    public ResponseEntity<CodeExecutionResponse> judgeInput(@RequestBody CodeExecutionRequest request) {
        String result = inputJudgeService.judgeInput(request.getLanguage(), request.getCode(), request.getInput());
        CodeExecutionResponse response = generateResponse(request.getExpected(), result);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(value = {ServerException.class})
    public ResponseEntity<CodeExecutionResponse> handleServerException(ServerException e) {
        CodeExecutionResponse response = new CodeExecutionResponse(null, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {RequestException.class})
    public ResponseEntity<CodeExecutionResponse> handleRequestException(RequestException e) {
        CodeExecutionResponse response = new CodeExecutionResponse(null, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {CodeExecutionException.class})
    public ResponseEntity<CodeExecutionResponse> handleCodeExecutionException(CodeExecutionException e) {
        CodeExecutionResponse response = new CodeExecutionResponse(null, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private CodeExecutionResponse generateResponse(String expected, String result) {
        if (!isCorrect(expected, result)) {
            throw new CodeExecutionException(JudgeResult.WRONG_ANSWER);
        }

        return new CodeExecutionResponse(result, JudgeResult.ACCEPTED.getMessage());
    }

    private boolean isCorrect(String output, String expected) {
        return output.equals(expected) || output.stripTrailing().equals(expected.stripTrailing());
    }
}