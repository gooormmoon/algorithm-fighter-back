package gooroommoon.algofi_core.algorithmproblem.testcase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestcaseService {

    private final TestcaseRepository testcaseRepository;

    public List<Testcase> getTestcases(Long algorithmProblemId) {
        return testcaseRepository.findAllByAlgorithmProblemId(algorithmProblemId);
    }
}
