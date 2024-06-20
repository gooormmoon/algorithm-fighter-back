package gooroommoon.algofi_compile.controller;

import gooroommoon.algofi_compile.dto.CodeExecutionRequest;
import gooroommoon.algofi_compile.dto.CodeExecutionResponse;
import gooroommoon.algofi_compile.service.CompileService;
import gooroommoon.algofi_compile.service.JudgeResult;
import gooroommoon.algofi_compile.service.language.CodeExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/compile")
public class CompileApiController {

    private final CompileService compileService;
    @PostMapping
    public ResponseEntity<CodeExecutionResponse> compile(@RequestBody CodeExecutionRequest request) {
        CodeExecutor codeExecutor = compileService.getCodeExecutor(request.getLanguage());

        Path path = codeExecutor.makeFileFromCode(request.getCode());

        Process process = compileService.executeCode(codeExecutor, path);

        StringBuilder output = insertInputAndGetOutput(request.getInput(), process, codeExecutor, path);

        String result = output.toString();
        CodeExecutionResponse response = generateResponse(request, result);
        return ResponseEntity.ok(response);
    }

    private StringBuilder insertInputAndGetOutput(String input, Process process, CodeExecutor codeExecutor, Path path) {
        try {
            return compileService.insertInputAndGetOutput(process, input);
        } finally {
            compileService.destroy(process);
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