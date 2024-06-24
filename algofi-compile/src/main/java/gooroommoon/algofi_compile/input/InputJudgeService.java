package gooroommoon.algofi_compile.input;

import gooroommoon.algofi_compile.judge.service.JudgeService;
import gooroommoon.algofi_compile.judge.service.language.CodeExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class InputJudgeService {
    private final JudgeService judgeService;

    public String judgeInput(String language, String code, String input){
        CodeExecutor codeExecutor = judgeService.getCodeExecutor(language);

        Path path = codeExecutor.makeFileFromCode(code);

        Process process = judgeService.executeCode(codeExecutor, path);

        StringBuilder output = insertInputAndGetOutput(process, input, codeExecutor, path);

        return output.toString();
    }

    private StringBuilder insertInputAndGetOutput(Process process, String input, CodeExecutor codeExecutor, Path path) {
        try {
            return judgeService.insertInputAndGetOutput(process, input);
        } finally {
            judgeService.destroy(process);
            codeExecutor.deleteFile(path);
        }
    }
}
