package gooroommoon.algofi_core.auth.member;

import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.MemberResponse;
import gooroommoon.algofi_core.auth.member.dto.TokenResponse;
import gooroommoon.algofi_core.auth.util.JwtUtil;
import gooroommoon.algofi_core.dto.ExceptionResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("dev")
public class MemberControllerIntegrateTests {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private JwtUtil jwtUtil;
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
    @DisplayName("멤버 등록")
    void registerMemberTest() {
        ResponseEntity<MemberResponse> response = registerMember();
        assertEquals("user", Objects.requireNonNull(response.getBody()).getId());
    }

    @Test
    @DisplayName("중복 아이디 멤버 등록")
    void duplicateMemberTest() {
        registerMember();

        MemberRequest.RegisterRequest registerRequest = MemberRequest.RegisterRequest.builder()
                .id("user").password("p").name("Mem").build();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MemberRequest.RegisterRequest> request = new HttpEntity<>(registerRequest, headers);
        ResponseEntity<ExceptionResponse> response = testRestTemplate.postForEntity("/api/member/register",
                request,
                ExceptionResponse.class);

        assertEquals("이미 존재하는 아이디입니다.", response.getBody().getMsg());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("회원가입 요청 값 누락")
    void missingRegisterValueTest() {
        MemberRequest.RegisterRequest registerRequest = MemberRequest.RegisterRequest.builder()
                .id("randomUser").name("Lee").build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MemberRequest.RegisterRequest> request = new HttpEntity<>(registerRequest, headers);
        ResponseEntity<ExceptionResponse> response = testRestTemplate.postForEntity("/api/member/register",
                request,
                ExceptionResponse.class);

        assertEquals("비밀번호는 필수 항목입니다.", response.getBody().getMsg());
    }

    @Test
    @DisplayName("로그인 및 토큰 검증")
    void loginTokenValidateTest() {
        registerMember();
        String token = loginMember("user", "pass");
        assertFalse(jwtUtil.isExpired(token));
    }

    @Test
    @DisplayName("내 정보 가져오기 요청의 토큰 잘못됨")
    void getMyInfoByWrongTokenTest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("itIsRandomString");

        HttpEntity<Objects> request = new HttpEntity<>(null, headers);
        ResponseEntity<Objects> response = testRestTemplate.exchange("/api/member", HttpMethod.GET, request, Objects.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("내 정보 가져오기")
    void getMyInfoTest() {
        registerMember();
        String token = loginMember("user", "pass");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Objects> request = new HttpEntity<>(null, headers);
        ResponseEntity<MemberResponse> response = testRestTemplate.exchange("/api/member", HttpMethod.GET, request, MemberResponse.class);

        assertEquals("Lee", response.getBody().getName());
    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePasswordTest() {
        registerMember();
        String token = loginMember("user", "pass");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<MemberRequest.ChangePasswordRequest> request = new HttpEntity<>(new MemberRequest.ChangePasswordRequest("newpass"), headers);
        testRestTemplate.exchange("/api/member/change-password", HttpMethod.PUT, request, String.class);

        assertNotNull(loginMember("user", "newpass"));
    }

    @Test
    @DisplayName("회원 정보 변경")
    void changeMemberInfoTest() {
        registerMember();
        String token = loginMember("user", "pass");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<MemberRequest> request = new HttpEntity<>(MemberRequest.builder().nickname("SadCat").build(), headers);
        ResponseEntity<MemberResponse> response = testRestTemplate.exchange("/api/member", HttpMethod.PUT, request, MemberResponse.class);

        assertEquals(response.getBody().getNickname(), "SadCat");
    }

    @Test
    @DisplayName("회원 탈퇴")
    void deleteMemberTest() {
        registerMember();
        String token = loginMember("user", "pass");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<MemberRequest.LoginRequest> request = new HttpEntity<>(new MemberRequest.LoginRequest("user", "pass"), headers);

        ResponseEntity<Objects> response = testRestTemplate.exchange("/api/member", HttpMethod.DELETE, request, Objects.class);

        ResponseEntity<MemberResponse> member = registerMember();
        assertEquals("user", member.getBody().getId());
    }

    @Test
    @DisplayName("다른 회원 조회")
    void getMemberInfoTest() {
        registerMember();
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<Objects> request = new HttpEntity<>(null, headers);
        ResponseEntity<MemberResponse> response = testRestTemplate.exchange("/api/members/user", HttpMethod.GET, request, MemberResponse.class);

        assertEquals("Lee", response.getBody().getName());
    }


    ResponseEntity<MemberResponse> registerMember() {
        MemberRequest.RegisterRequest registerRequest = MemberRequest.RegisterRequest.builder()
                .id("user").password("pass").name("Lee").build();

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
