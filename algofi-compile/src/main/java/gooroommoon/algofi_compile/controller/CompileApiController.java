package gooroommoon.algofi_compile.controller;

import gooroommoon.algofi_compile.dto.CodeExecutionRequest;
import gooroommoon.algofi_compile.dto.CodeExecutionResponse;
import gooroommoon.algofi_compile.service.CompileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/compile")
public class CompileApiController {

    private final CompileService compileService;
    @PostMapping
    public ResponseEntity<CodeExecutionResponse> compile(@RequestBody CodeExecutionRequest request) {
        CodeExecutionResponse response = compileService.runCode(request);
        return ResponseEntity.ok(response);
    }

}