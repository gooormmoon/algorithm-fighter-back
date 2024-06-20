package gooroommoon.algofi_compile.input;

import gooroommoon.algofi_compile.input.dto.CodeExecutionRequest;
import gooroommoon.algofi_compile.input.dto.CodeExecutionResponse;
import gooroommoon.algofi_compile.judge.service.JudgeService;
import gooroommoon.algofi_compile.judge.service.JudgeResult;
import gooroommoon.algofi_compile.judge.service.language.CodeExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            return new CodeExecutionResponse(result, JudgeResult.WRONG_ANSWER.getMessage());
        }

        return new CodeExecutionResponse(result, JudgeResult.ACCEPTED.getMessage());
    }
}