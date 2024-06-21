package gooroommoon.algofi_core.chat.game.session;

import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.auth.member.MemberRepository;
import gooroommoon.algofi_core.auth.member.MemberRole;
import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.TokenResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameSessionTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void addMember() {
        memberRepository.delete(memberRepository.findByLoginId("user").get());

        Member member = Member.builder()
                .name("A")
                .nickname("A")
                .loginDate(LocalDateTime.now())
                .loginId("user")
                .password("pass")
                .role(MemberRole.USER)
                .build();
        memberRepository.save(member);

        MemberRequest.LoginRequest loginRequest = MemberRequest.LoginRequest.builder()
                .id("user").password("pass").build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MemberRequest.LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<TokenResponse> response = testRestTemplate.postForEntity("/api/member/login", request, TokenResponse.class);
        System.out.println(response.getBody().getAccessToken());
    }

}