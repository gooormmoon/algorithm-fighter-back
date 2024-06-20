package gooroommoon.algofi_core.algorithmproblem;

import gooroommoon.algofi_core.algorithmproblem.dto.AlgorithmproblemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlgorithmproblemService {

    private final AlgorithmproblemRepository algorithmproblemRepository;

    /**
     * 랜덤으로 가져오는 서비스 작성 -> repository에서 해결
     */
    public AlgorithmproblemResponse getRandom(String level) {
        Algorithmproblem randomAlgorithmproblem = algorithmproblemRepository.getRandomAlgorithmproblem(level);

        return fromAlgorithmproblem(randomAlgorithmproblem);
    }

    private AlgorithmproblemResponse fromAlgorithmproblem(Algorithmproblem Algorithmproblem) {
        return AlgorithmproblemResponse.builder()
                .title(Algorithmproblem.getTitle())
                .content(Algorithmproblem.getContent())
                .level(Algorithmproblem.getLevel())
                .recommend_time(Algorithmproblem.getRecommend_time())
                .build();
    }
}
