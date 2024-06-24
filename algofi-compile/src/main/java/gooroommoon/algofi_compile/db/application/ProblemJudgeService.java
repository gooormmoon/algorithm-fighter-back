package gooroommoon.algofi_compile.db.application;

import gooroommoon.algofi_compile.db.domain.TestCaseRepository;
import gooroommoon.algofi_compile.db.domain.entity.TestCase;
import gooroommoon.algofi_compile.db.presentation.dto.ProblemJudgeResponse;
import gooroommoon.algofi_compile.judge.exception.CodeExecutionException;
import gooroommoon.algofi_compile.judge.service.JudgeResult;
import gooroommoon.algofi_compile.judge.service.JudgeService;
import gooroommoon.algofi_compile.judge.service.language.CodeExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemJudgeService {
    private final TestCaseRepository testCaseRepository;
    private final JudgeService judgeService;

    @Transactional(readOnly = true)
    public synchronized ProblemJudgeResponse judgeProblem(String language, Long algorithmProblemId, String code) {

        CodeExecutor codeExecutor = judgeService.getCodeExecutor(language);

        Path path = codeExecutor.makeFileFromCode(code);

        List<TestCase> testCases = testCaseRepository.findAllByAlgorithmProblemId(algorithmProblemId);

        try {
            testCases.forEach(testCase -> {
                Process process = judgeService.executeCode(codeExecutor, path);
                judge(testCase, process);
            });
        } finally {
            codeExecutor.deleteFile(path);
        }

        return new ProblemJudgeResponse(JudgeResult.ACCEPTED.getMessage());
    }

    private void judge(TestCase testCase, Process process) {
        try {
            String input = testCase.getTestInput();
            String expected = testCase.getTestOutput();

            StringBuilder output = judgeService.insertInputAndGetOutput(process, input);

            if (!isCorrect(output.toString(), expected)) {
                throw new CodeExecutionException(JudgeResult.WRONG_ANSWER);
            }
        } finally {
            judgeService.destroy(process);
        }
    }

    private boolean isCorrect(String output, String expected) {
        return output.equals(expected) || output.stripTrailing().equals(expected);
    }
}
