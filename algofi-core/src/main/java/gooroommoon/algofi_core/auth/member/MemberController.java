package gooroommoon.algofi_core.auth.member;

import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.MemberResponse;
import gooroommoon.algofi_core.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<MemberResponse> register(@RequestBody MemberRequest memberRequest) {
        return ResponseEntity.ok().body(memberService.register(memberRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> loginRequest) {
        Member member = memberService.authenticate(loginRequest.get("id"), loginRequest.get("password"));
        return ResponseEntity.ok().body("\"access_token\": %s".formatted(jwtUtil.createToken(member.getLoginId())));
    }


    @GetMapping("")
    public ResponseEntity<MemberResponse> myInfo(Authentication auth) {
        MemberResponse memberResponse = memberService.getMemberResponseByLoginId(auth.getName());
        return ResponseEntity.ok().body(memberResponse);
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(Authentication auth, @RequestBody Map<String, String> changePasswordRequest) {
        memberService.updateMemberPasswordByLoginId(auth.getName(), changePasswordRequest.get("password"));
        return ResponseEntity.ok().body("");
    }

    @PutMapping("")
    public ResponseEntity<MemberResponse> updateInfo(Authentication auth, @RequestBody MemberRequest memberRequest) {
        MemberResponse memberResponse = memberService.updateMemberInfoByLoginId(auth.getName(), memberRequest);
        return ResponseEntity.ok().body(memberResponse);
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteMember(Authentication auth) {
        memberService.deleteMemberByLoginId(auth.getName());
        return ResponseEntity.ok().body("");
    }
}
