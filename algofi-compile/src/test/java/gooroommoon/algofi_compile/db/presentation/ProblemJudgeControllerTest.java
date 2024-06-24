package gooroommoon.algofi_compile.db.presentation;

import gooroommoon.algofi_compile.db.application.ProblemJudgeService;
import gooroommoon.algofi_compile.db.domain.TestCaseRepository;
import gooroommoon.algofi_compile.db.domain.entity.AlgorithmProblem;
import gooroommoon.algofi_compile.db.domain.entity.TestCase;
import gooroommoon.algofi_compile.db.presentation.dto.ProblemJudgeRequest;
import gooroommoon.algofi_compile.db.presentation.dto.ProblemJudgeResponse;
import gooroommoon.algofi_compile.judge.exception.CodeExecutionException;
import gooroommoon.algofi_compile.judge.service.JudgeResult;
import gooroommoon.algofi_compile.judge.service.JudgeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProblemJudgeControllerTest {

    @Mock
    private TestCaseRepository testCaseRepository;

    @Test
    void success() {
        //given
        AlgorithmProblem algorithmProblem = new AlgorithmProblem(1L, "문자 찾기","1","한 개의 문자열을 입력받고,특정 문자를 입력받아 해당 특정문자가 입력받은 문자열에 몇 개 존재하는지 알아내는 프로그램을 작성하세요. 대소문자를 구분하지 않습니다.문자열의 길이는 100을 넘지 않습니다.",10);
        TestCase testCase = new TestCase(1L, "computerprogramming r","3", algorithmProblem);
        when(testCaseRepository.findAllByAlgorithmProblemId(any()))
                .thenReturn(List.of(testCase));

        JudgeService judgeService = new JudgeService();

        ProblemJudgeService problemJudgeService = new ProblemJudgeService(testCaseRepository, judgeService);
        ProblemJudgeController problemJudgeController = new ProblemJudgeController(problemJudgeService);

        //when
        ProblemJudgeRequest request = new ProblemJudgeRequest(1L, "java",
                """
                        import java.util.Scanner;
                        public class Main {
                            public int solution(String str, char c){
                                int answer = 0;
                                str = str.toUpperCase();
                                c = Character.toUpperCase(c);
                                for(char x : str.toCharArray()){
                                    if(x == c) answer++;
                                }
                                return answer;
                            }

                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                Main T = new Main();
                                String str = sc.next();
                                char c = sc.next().charAt(0);

                                System.out.println(T.solution(str, c));
                            }
                        }""");
        ResponseEntity<ProblemJudgeResponse> response = problemJudgeController.judgeProblem(request);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(JudgeResult.ACCEPTED.getMessage());
    }

    @Test
    void concurrencyTest() throws InterruptedException {
        //given
        AlgorithmProblem algorithmProblem = new AlgorithmProblem(1L, "문자 찾기","1","한 개의 문자열을 입력받고,특정 문자를 입력받아 해당 특정문자가 입력받은 문자열에 몇 개 존재하는지 알아내는 프로그램을 작성하세요. 대소문자를 구분하지 않습니다.문자열의 길이는 100을 넘지 않습니다.",10);
        TestCase testCase = new TestCase(1L, "computerprogramming r","3", algorithmProblem);
        when(testCaseRepository.findAllByAlgorithmProblemId(any()))
                .thenReturn(List.of(testCase));

        JudgeService judgeService = new JudgeService();

        ProblemJudgeService problemJudgeService = new ProblemJudgeService(testCaseRepository, judgeService);
        ProblemJudgeController problemJudgeController = new ProblemJudgeController(problemJudgeService);

        int threadNum = 2;
        CountDownLatch doneSignal = new CountDownLatch(threadNum);
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);

        ProblemJudgeRequest request1 = new ProblemJudgeRequest(1L, "java",
                """
                        import java.util.Scanner;

                        public class Main {
                            public int solution(String str, char c){
                                int answer = 0;
                                str = str.toUpperCase();
                                c = Character.toUpperCase(c);
                                for(char x : str.toCharArray()){
                                    if(x == c) answer++;
                                }
                                return answer;
                            }

                            public static void main(String[] args) {
                                Scanner sc = new Scanner(System.in);
                                Main T = new Main();
                                String str = sc.next();
                                char c = sc.next().charAt(0);

                                System.out.println(T.solution(str, c));
                            }
                        }""");

        ProblemJudgeRequest request2 = new ProblemJudgeRequest(1L, "java",
                """

                        public class Main {
                            public static void main(String[] args) {
                                System.out.println();
                            }
                        }""");

        AtomicBoolean success1 = new AtomicBoolean();
        AtomicBoolean success2 = new AtomicBoolean();

        //when

        executorService.execute(() -> {
            try {
                problemJudgeController.judgeProblem(request1);
                success1.set(true);
            } catch (CodeExecutionException e) {
                success1.set(false);
            } finally {
                doneSignal.countDown();
            }
        });

        executorService.execute(() -> {
            try {
                problemJudgeController.judgeProblem(request2);
                success2.set(true);
            } catch (CodeExecutionException e) {
                success2.set(false);
            } finally {
                doneSignal.countDown();
            }
        });

        doneSignal.await();
        executorService.shutdown();

        //then
        assertAll(
                () -> assertThat(success1.get()).isTrue(),
                () -> assertThat(success2.get()).isFalse()
        );
    }
}