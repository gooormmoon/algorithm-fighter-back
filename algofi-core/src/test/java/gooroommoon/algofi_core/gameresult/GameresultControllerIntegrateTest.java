package gooroommoon.algofi_core.gameresult;

import gooroommoon.algofi_core.algorithmproblem.Algorithmproblem;
import gooroommoon.algofi_core.algorithmproblem.AlgorithmproblemRepository;
import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.auth.member.MemberRepository;
import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.MemberResponse;
import gooroommoon.algofi_core.auth.member.dto.TokenResponse;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.repository.ChatroomRepository;
import gooroommoon.algofi_core.chat.service.ChatroomService;
import gooroommoon.algofi_core.gameresult.dto.GameresultResponse;
import gooroommoon.algofi_core.gameresult.dto.GameresultsResponse;
import gooroommoon.algofi_core.gameresult.membergameresult.MemberGameresult;
import gooroommoon.algofi_core.gameresult.membergameresult.MemberGameresultRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class GameresultControllerIntegrateTest {

    /**
     * 테스트 실행 시 application-dev.properties에서 spring.jpa.hibernate.ddl-auto=create  (update --> create로 변경해야함.)
     */

    public static final String HOST_CODE = "hostCode";
    public static final String GUEST_CODE = "guestCode";
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    AlgorithmproblemRepository algorithmproblemRepository;

    @Autowired
    ChatroomService chatRoomService;

    @Autowired
    GameresultRepository gameresultRepository;

    @Autowired
    MemberGameresultRepository memberGameresultRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    private ChatroomRepository chatroomRepository;

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
    @DisplayName("등록된 멤버의 특정한 게임 결과 조회")
    void getGameresult() {
        registerMember();
        String token = loginMember("test", "pass");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        Algorithmproblem algorithmproblem = new Algorithmproblem("title", "1", "content!", 20);
        algorithmproblemRepository.save(algorithmproblem);

        Chatroom chatroom = new Chatroom();
        chatroom.setChatroomId("global");
        chatroom.setChatroomName("1번방");
        chatroomRepository.save(chatroom);

        Member member = memberRepository.findByLoginId("test").orElseThrow(() ->
                new UsernameNotFoundException("유저가 존재하지 않습니다."));

        Gameresult gameresult = new Gameresult(20, HOST_CODE, GUEST_CODE, algorithmproblem, chatroom);
        gameresultRepository.save(gameresult);
        Long gameresultId = gameresult.getGameresultId();

        MemberGameresult memberGameresult = new MemberGameresult(member, gameresult);
        memberGameresultRepository.save(memberGameresult);

        HttpEntity<Object> request = new HttpEntity<>(headers);

        ResponseEntity<GameresultResponse> response = testRestTemplate.exchange("/api/game/member/" + gameresultId, HttpMethod.GET, request, GameresultResponse.class);

//        assertNotNull(response.getBody());
        assertEquals(HOST_CODE, response.getBody().getHostCodeContent());
        assertEquals(GUEST_CODE, response.getBody().getGuestCodeContent());
    }

    @Test
    @DisplayName("등록된 멤버의 모든 게임 결과 조회")
    void getGameresults() {
        registerMember();
        String token = loginMember("test", "pass");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        Algorithmproblem algorithmproblem = new Algorithmproblem("title", "1", "content!", 20);
        algorithmproblemRepository.save(algorithmproblem);

        Chatroom chatroom = new Chatroom();
        chatroom.setChatroomId("global");
        chatroom.setChatroomName("1번방");
        chatroomRepository.save(chatroom);
        Chatroom chatroom2 = new Chatroom();
        chatroom2.setChatroomId("local");
        chatroom2.setChatroomName("2번방");
        chatroomRepository.save(chatroom2);

        Member member = memberRepository.findByLoginId("test").orElseThrow(() ->
                new UsernameNotFoundException("유저가 존재하지 않습니다."));

        Gameresult gameresult = new Gameresult(20, HOST_CODE, GUEST_CODE, algorithmproblem, chatroom);
        Gameresult gameresult2 = new Gameresult(20, HOST_CODE, GUEST_CODE, algorithmproblem, chatroom2);
        gameresultRepository.save(gameresult);
        gameresultRepository.save(gameresult2);

        MemberGameresult memberGameresult = new MemberGameresult(member, gameresult);
        MemberGameresult memberGameresult2 = new MemberGameresult(member, gameresult2);

        memberGameresultRepository.save(memberGameresult);
        memberGameresultRepository.save(memberGameresult2);

        HttpEntity<Objects> request = new HttpEntity<>(null, headers);

        ResponseEntity<List<GameresultsResponse>> response = testRestTemplate.exchange(
                "/api/game/member/gameresults", HttpMethod.GET, request, new ParameterizedTypeReference<List<GameresultsResponse>>() {});

        assertNotNull(response.getBody());
        assertEquals(response.getBody().size(), 2);
    }

    ResponseEntity<MemberResponse> registerMember() {
        MemberRequest.RegisterRequest registerRequest = MemberRequest.RegisterRequest.builder()
                .id("test").password("pass").name("Lee").build();

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
