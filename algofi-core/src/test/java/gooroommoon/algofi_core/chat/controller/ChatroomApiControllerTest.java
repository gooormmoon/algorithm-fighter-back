package gooroommoon.algofi_core.chat.controller;

import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.MemberResponse;
import gooroommoon.algofi_core.auth.member.dto.TokenResponse;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.service.ChatroomService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("dev")
public class ChatroomApiControllerTest {


    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    ChatroomService chatRoomService;

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
    @DisplayName("chatroom api 테스트")
    void ApiChatroom() {
        registerMember();
        String token = loginMember("user", "pass");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        chatRoomService.saveChatRoom("방1번");
        chatRoomService.saveChatRoom("방2번");


        HttpEntity<Objects> request = new HttpEntity<>(null, headers);
        ResponseEntity<List<Chatroom>> response = testRestTemplate.exchange(
                "/api/chat/list", HttpMethod.GET, request, new ParameterizedTypeReference<List<Chatroom>>() {});

        assertEquals(response.getBody().size(), 2);
        assertEquals(response.getBody().get(0).getChatroomName(), "방1번");

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
