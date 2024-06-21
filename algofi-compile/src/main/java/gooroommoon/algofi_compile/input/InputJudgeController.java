package gooroommoon.algofi_compile.input;

import gooroommoon.algofi_compile.input.dto.CodeExecutionRequest;
import gooroommoon.algofi_compile.input.dto.CodeExecutionResponse;
import gooroommoon.algofi_compile.judge.exception.CodeExecutionException;
import gooroommoon.algofi_compile.judge.exception.RequestException;
import gooroommoon.algofi_compile.judge.exception.ServerException;
import gooroommoon.algofi_compile.judge.service.JudgeResult;
import gooroommoon.algofi_compile.judge.service.JudgeService;
import gooroommoon.algofi_compile.judge.service.language.CodeExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/judge-input")
public class InputJudgeController {

    private final JudgeService judgeService;
    @PostMapping
    public ResponseEntity<CodeExecutionResponse> judgeInput(@RequestBody CodeExecutionRequest request) {
        CodeExecutor codeExecutor = judgeService.getCodeExecutor(request.getLanguage());

        Path path = codeExecutor.makeFileFromCode(request.getCode());

        Process process = judgeService.executeCode(codeExecutor, path);

        StringBuilder output = insertInputAndGetOutput(request.getInput(), process, codeExecutor, path);

        String result = output.toString();
        CodeExecutionResponse response = generateResponse(request, result);
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

    private StringBuilder insertInputAndGetOutput(String input, Process process, CodeExecutor codeExecutor, Path path) {
        try {
            return judgeService.insertInputAndGetOutput(process, input);
        } finally {
            judgeService.destroy(process);
            codeExecutor.deleteFile(path);
        }
    }

    private CodeExecutionResponse generateResponse(CodeExecutionRequest request, String result) {
        if (!result.equals(request.getExpected())) {
            throw new CodeExecutionException(JudgeResult.WRONG_ANSWER);
        }

        return new CodeExecutionResponse(result, JudgeResult.ACCEPTED.getMessage());
    }
}