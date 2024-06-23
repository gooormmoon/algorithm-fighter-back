package gooroommoon.algofi_core.gameresult;

import gooroommoon.algofi_core.algorithmproblem.Algorithmproblem;
import gooroommoon.algofi_core.algorithmproblem.AlgorithmproblemRepository;
import gooroommoon.algofi_core.auth.member.MemberService;
import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.MemberResponse;
import gooroommoon.algofi_core.game.session.GameSession;
import gooroommoon.algofi_core.game.session.GameSessionService;
import gooroommoon.algofi_core.game.session.dto.GameCodeRequest;
import gooroommoon.algofi_core.game.session.dto.GameSessionUpdateRequest;
import gooroommoon.algofi_core.gameresult.dto.GameresultResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("dev")
public class GameResultSaveIntegrateTest {

    /**
     * 테스트 검증 시 profiles.default = dev , spring.jpa.hibernate.ddl-auto=create 설정 하고 실행해야함
     */

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AlgorithmproblemRepository algorithmproblemRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private GameSessionService gameSessionService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private GameresultService gameresultService;

    private Long sequence = 0L;

    @BeforeEach
    void setup() {
        for (int i = 1; i <= 5; i++) {
            Algorithmproblem algorithmproblem = Algorithmproblem.builder()
                    .title("title" + i)
                    .level("3")
                    .content("content" + i)
                    .recommend_time(30 + i)
                    .build();
            algorithmproblemRepository.save(algorithmproblem);
        }

        sequence += 1L;
    }

    @AfterEach
    void afterEach() {
        //매 테스트마다 테이블 초기화
        //H2 Database
        List<String> truncateQueries = jdbcTemplate.queryForList(
                "SELECT CONCAT('TRUNCATE TABLE ', TABLE_NAME, ';') AS q FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'", String.class);
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        truncateQueries.forEach(jdbcTemplate::execute);
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
        //MySQL
//        jdbcTemplate.execute("SET foreign_key_checks = 0;");
//        jdbcTemplate.execute("TRUNCATE TABLE member");
//        jdbcTemplate.execute("SET foreign_key_checks = 1;");
    }


    @Test
    @DisplayName("저장 요청 시 게임결과 저장 검증")
    void SuccessSaveGameResult() throws ExecutionException, InterruptedException, TimeoutException {
        ResponseEntity<MemberResponse> user1 = registerMember("user1");
        ResponseEntity<MemberResponse> user2 = registerMember("user2");

        MemberResponse user1info = memberService.getInfo(user1.getBody().getId());
        MemberResponse user2info = memberService.getInfo(user2.getBody().getId());

        //게임생성
        GameSessionUpdateRequest gameSessionUpdateRequest = GameSessionUpdateRequest.builder()
                .title("user1님의 방")
                .problemLevel("3")
                .timerTime(10)
                .build();

        gameSessionService.createSession(user1info.getId(), gameSessionUpdateRequest);

        //게임참가
        gameSessionService.addPlayer(user1info.getId(), user2info.getId());

        //게임준비
        gameSessionService.playerReady(user1info.getId());
        gameSessionService.playerReady(user2info.getId());

        //게임시작
        gameSessionService.startGame(user1info.getId());

        //게임종료
        GameSession session = gameSessionService.getSession(user1info.getId());
        gameSessionService.closeGame(session, user1info.getId());

        //게임결과저장
        GameCodeRequest gameCodeRequestUser1 = GameCodeRequest.builder()
                .code("user1Code~~~~")
                .language("java")
                .build();

        GameCodeRequest gameCodeRequestUser2 = GameCodeRequest.builder()
                .code("user2Code~~~~")
                .language("java")
                .build();

        gameSessionService.savePlayerCode(user1info.getId(),gameCodeRequestUser1);
        gameSessionService.savePlayerCode(user2info.getId(),gameCodeRequestUser2);

        //게임결과 저장 검증로직
        List<Gameresult> allGameresult = gameresultService.findAllGameresult();
        assertEquals(allGameresult.size(), 1);
    }

    @Test
    @DisplayName("게임 속 멤버들이 동일한 게임결과 저장 검증")
    void OneToOneGameToGameResult() {
        ResponseEntity<MemberResponse> user3 = registerMember("user3");
        ResponseEntity<MemberResponse> user4 = registerMember("user4");

        MemberResponse user3info = memberService.getInfo(user3.getBody().getId());
        MemberResponse user4info = memberService.getInfo(user4.getBody().getId());
        //게임생성
        GameSessionUpdateRequest gameSessionUpdateRequest = GameSessionUpdateRequest.builder()
                .title("user1님의 방")
                .problemLevel("3")
                .timerTime(10)
                .build();

        gameSessionService.createSession(user3info.getId(), gameSessionUpdateRequest);

        //게임참가
        gameSessionService.addPlayer(user3info.getId(), user4info.getId());

        //게임준비
        gameSessionService.playerReady(user3info.getId());
        gameSessionService.playerReady(user4info.getId());

        //게임시작
        gameSessionService.startGame(user3info.getId());

        //게임종료
        GameSession session = gameSessionService.getSession(user3info.getId());
        gameSessionService.closeGame(session, user3info.getId());

        //게임결과저장
        GameCodeRequest gameCodeRequestUser1 = GameCodeRequest.builder()
                .code("user1Code~~~~")
                .language("java")
                .build();

        GameCodeRequest gameCodeRequestUser2 = GameCodeRequest.builder()
                .code("user2Code~~~~")
                .language("java")
                .build();

        gameSessionService.savePlayerCode(user3info.getId(),gameCodeRequestUser1);
        gameSessionService.savePlayerCode(user4info.getId(),gameCodeRequestUser2);


        long gameNumber = ++sequence;
        //유저3 게임결과
        GameresultResponse gameresultByUser3 = gameresultService.findGameresult(user3info.getId(), gameNumber);
        //유저4 게임결과
        GameresultResponse gameresultByUser4 = gameresultService.findGameresult(user4info.getId(), gameNumber);

        assertEquals(gameresultByUser3.getHostCodeContent(), gameresultByUser4.getHostCodeContent());
        assertEquals(gameresultByUser3.getGuestCodeContent(), gameresultByUser4.getGuestCodeContent());

        //게임 결과 러닝타임 검증
        //테스트마다 러닝타임이 달라서 0이 아닌값이 들어가는 것으로 대신 검증
        assertNotEquals(gameresultByUser3.getRunningTime(), 0);
    }

    ResponseEntity<MemberResponse> registerMember(String loginId) {
        MemberRequest.RegisterRequest registerRequest = MemberRequest.RegisterRequest.builder()
                .id(loginId).password("pass").name("Lee").build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MemberRequest.RegisterRequest> request = new HttpEntity<>(registerRequest, headers);
        return testRestTemplate.postForEntity("/api/member/register",
                request,
                MemberResponse.class);
    }

}
