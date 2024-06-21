package gooroommoon.algofi_core.algorithmproblem;

import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.MemberResponse;
import gooroommoon.algofi_core.auth.member.dto.TokenResponse;
import gooroommoon.algofi_core.auth.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class AlgorithmproblemRandomControllerTest {


    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    @DisplayName("문제 랜덤 생성")
    void createRandomProblem() {
        registerMember("host");
        registerMember("guest");

        String hostToken = loginMember("host", "pass");
        String guestToken = loginMember("guest", "pass");

        HttpHeaders hostHeaders = new HttpHeaders();
        hostHeaders.setBearerAuth(hostToken);

        HttpHeaders guestHeaders = new HttpHeaders();
        guestHeaders.setBearerAuth(guestToken);


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
