package gooroommoon.algofi_core.algorithmproblem;

import gooroommoon.algofi_core.algorithmproblem.dto.AlgorithmProblemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlgorithmProblemService {

    private final AlgorithmProblemRepository algorithmProblemRepository;

    /**
     * 랜덤으로 가져오는 서비스 작성 -> repository에서 해결
     */
    public AlgorithmProblemResponse getRandom(String level) {
        AlgorithmProblem randomAlgorithmProblem = algorithmProblemRepository.getRandomAlgorithmProblem(level);

        return fromAlgorithmProblem(randomAlgorithmProblem);
    }

    private AlgorithmProblemResponse fromAlgorithmProblem(AlgorithmProblem algorithmProblem) {
        return AlgorithmProblemResponse.builder()
                .title(algorithmProblem.getTitle())
                .content(algorithmProblem.getContent())
                .level(algorithmProblem.getLevel())
                .recommend_time(algorithmProblem.getRecommend_time())
                .build();
    }
}
