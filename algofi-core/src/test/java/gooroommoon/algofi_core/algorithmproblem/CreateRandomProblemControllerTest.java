package gooroommoon.algofi_core.algorithmproblem;

import gooroommoon.algofi_core.algorithmproblem.dto.AlgorithmproblemResponse;
import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.MemberResponse;
import gooroommoon.algofi_core.auth.member.dto.TokenResponse;
import gooroommoon.algofi_core.auth.util.JwtUtil;
import gooroommoon.algofi_core.game.session.dto.GameSessionJoinRequest;
import gooroommoon.algofi_core.game.session.dto.GameSessionUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("dev")
@Slf4j
public class CreateRandomProblemControllerTest {

    @Autowired
    private AlgorithmproblemService algorithmproblemService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private AlgorithmproblemRepository algorithmproblemRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private final String WEBSOCKET_SESSION = "/user/queue/game/session";

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
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
    @DisplayName("레벨에 맞는 문제 random select 테스트")
    void createRandomProblem() {
        //given
        for (int i = 1; i <= 5; i++) {
            Algorithmproblem algorithmproblem = Algorithmproblem.builder()
                    .title("title" + i)
                    .level("3")
                    .content("content" + i)
                    .recommend_time(30 + i)
                    .build();

            algorithmproblemRepository.save(algorithmproblem);
        }

        Algorithmproblem algorithmproblem = Algorithmproblem.builder()
                .title("title" + 6)
                .level("1")
                .content("content" + 6)
                .recommend_time(30 + 6)
                .build();

        //when
        AlgorithmproblemResponse randomProblem = algorithmproblemService.getRandom("3");

        //then
        for (int i = 0; i < 100; i++) {
            Assertions.assertNotEquals(randomProblem.getTitle(), "title6");
        }
    }

    @Test
    @DisplayName("문제 요청 시 올바른 데이터 리턴 검증")
    void SuccessSendProblem() throws ExecutionException, InterruptedException, TimeoutException {
        registerMember("user");
        registerMember("member");
        String token = loginMember("user", "pass");
        String memberToken = loginMember("member", "pass");
        assertFalse(jwtUtil.isExpired(token));

        String websocketUri = "ws://localhost:" + port + "/game";

        WebSocketHttpHeaders connectHeaders = new WebSocketHttpHeaders();
        connectHeaders.add("Authorization", "Bearer " + token);
        connectHeaders.add("userName", "user");

//        StompHeaders connectStompHeaders =

        //여기는 "user의 세션"
        //여기서 인증정보를 넘겨줘야하나?
        StompSession stompSession = stompClient.connect(websocketUri, connectHeaders, new StompSessionHandlerAdapter() {
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                log.info("{} 님이 게임방에 입장하셨습니다.", connectHeaders.get("userName"));
            }
        }).get(1, TimeUnit.SECONDS);

        StompHeaders subscribeHeaders = new StompHeaders();
        subscribeHeaders.setDestination("/user/queue/game/session");
        stompSession.subscribe(subscribeHeaders, new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {
                // 메시지 타입을 결정합니다.
                return Type.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // 수신된 메시지를 처리합니다.
            }
        });

        GameSessionUpdateRequest gameSessionUpdateRequest = GameSessionUpdateRequest.builder()
                .title("user님의 방")
                .problemLevel("3")
                .timerTime(10)
                .build();

        StompHeaders sendCreateHeaders = new StompHeaders();
        sendCreateHeaders.setDestination("/app/game/create");
        sendCreateHeaders.add("Authorization", "Bearer " + memberToken);

        //TODO 게임 생성
        stompSession.send(sendCreateHeaders, gameSessionUpdateRequest);
        log.info("게임생성");

        WebSocketHttpHeaders connectHeadersMember = new WebSocketHttpHeaders();
        connectHeadersMember.add("Authorization", "Bearer " + memberToken);
        connectHeadersMember.add("userName", "member");

        //여기는 "member"의 세션
        StompSession stompSessionMember = stompClient.connect(websocketUri, connectHeadersMember, new StompSessionHandlerAdapter() {
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                log.info("{} 님이 게임방에 입장하셨습니다.", connectHeadersMember.get("userName"));
            }
        }).get(1, TimeUnit.SECONDS);

        //Member 입장
        stompSessionMember.subscribe(subscribeHeaders, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                // 메시지 타입을 결정합니다.
                return AlgorithmproblemResponse.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                // 수신된 메시지를 처리합니다.
            }
        });

        StompHeaders sendJoinHeaders = new StompHeaders();
        sendJoinHeaders.setDestination("/app/game/join");
        sendJoinHeaders.add("Authorization", "Bearer " + memberToken);
        sendJoinHeaders.setLogin("member");

        GameSessionJoinRequest gameSessionJoinRequest = GameSessionJoinRequest.builder()
                .hostId("user").build();

        // 게임 참가 요청을 서버로 전송합니다.
        //TODO 게임 참가
        stompSessionMember.send(sendJoinHeaders, gameSessionJoinRequest);
        log.info("게임 참가");

        for (int i = 1; i <= 5; i++) {
            Algorithmproblem algorithmproblem = Algorithmproblem.builder()
                    .title("title" + i)
                    .level("3")
                    .content("content" + i)
                    .recommend_time(30 + i)
                    .build();
            algorithmproblemRepository.save(algorithmproblem);
        }

        //TODO 게임 준비
        StompHeaders sendReadyHeaders = new StompHeaders();
        sendReadyHeaders.setDestination("/app/game/ready");
        sendReadyHeaders.add("Authorization", "Bearer " + memberToken);
        sendReadyHeaders.setLogin("user");

        stompSession.send(sendReadyHeaders, null);

        StompHeaders sendReadyHeaders2 = new StompHeaders();
        sendReadyHeaders2.setDestination("/app/game/ready");
        sendReadyHeaders2.add("Authorization", "Bearer " + token);
        sendReadyHeaders.setLogin("member");

        stompSessionMember.send(sendReadyHeaders2, null);

        StompHeaders sendHeaders = new StompHeaders();
        sendHeaders.setDestination("/app/game/start");
        sendHeaders.add("Authorization", "Bearer " + token);
        sendHeaders.setLogin("user");

        //TODO 게임 시작
        stompSession.send(sendHeaders,null);

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

    String loginMember(String loginId, String password) {
        MemberRequest.LoginRequest loginRequest = MemberRequest.LoginRequest.builder()
                .id(loginId).password(password).build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MemberRequest.LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<TokenResponse> response = testRestTemplate.postForEntity("/api/member/login", request, TokenResponse.class);
        return response.getBody().getAccessToken();
    }
}
