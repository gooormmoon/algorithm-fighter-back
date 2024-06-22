package gooroommoon.algofi_core.algorithmproblem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlgorithmproblemService {

    private final AlgorithmproblemRepository algorithmproblemRepository;

    /**
     * 랜덤으로 가져오는 서비스 작성 -> repository에서 해결
     */
    public Algorithmproblem getRandom(String level) {
        List<Algorithmproblem> findAllProblems = algorithmproblemRepository.findAllByLevel(level);

        //random으로 값을
        Random random = new Random();
        int size = findAllProblems.size();

        return findAllProblems.get(random.nextInt(size));
    }

//    private List<AlgorithmproblemResponse> fromAlgorithmproblems(List<Algorithmproblem> Algorithmproblems) {
//        List<AlgorithmproblemResponse> results = new ArrayList<>();
//        for (Algorithmproblem algorithmproblem : Algorithmproblems) {
//            algorithmproblem.builder()
//                    .title(algorithmproblem.getTitle())
//                    .level(algorithmproblem.getLevel())
//                    .content(algorithmproblem.getContent())
//                    .build();
//        }
//
//        return results;
//    }
}
