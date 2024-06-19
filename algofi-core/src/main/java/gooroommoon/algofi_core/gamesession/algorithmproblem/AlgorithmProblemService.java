package gooroommoon.algofi_core.gamesession.algorithmproblem;

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
    public AlgorithmProblem getRandom(String level) {
//        level이 가공 되어서 들어오면
//        return algorithmProblemRepository.getRandomAlgorithmProblem(level);

        //level이 json 형태로 들어오면 getLevel메서드 필요x
        String levelValue = getLevel(level);
        return algorithmProblemRepository.getRandomAlgorithmProblem(levelValue);
    }

    /**
     * Json형태의 level값에서 level의 value를 가공
     */
    public String getLevel(String level){
        String result = "";
        for (char x : level.toCharArray()) {
            if(48<=x && x <=57){
                result += x;
            }
        }
        return result;
    }

}
