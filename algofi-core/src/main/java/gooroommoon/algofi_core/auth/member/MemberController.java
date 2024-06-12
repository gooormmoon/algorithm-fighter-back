package gooroommoon.algofi_core.auth.member;

import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.MemberResponse;
import gooroommoon.algofi_core.auth.util.JwtUtil;
import gooroommoon.algofi_core.dto.ExceptionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public ResponseEntity<MemberResponse> register(@RequestBody @Valid MemberRequest.RegisterRequest registerRequest) {
        return ResponseEntity.ok().body(memberService.register(registerRequest));
    }

    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicateLoginIdException(DuplicateLoginIdException exception) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(exception.getMessage()));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid MemberRequest.LoginRequest loginRequest) {
        Member member = memberService.authenticate(loginRequest.getId(), loginRequest.getPassword());
        return ResponseEntity.ok().body("{\"access_token\": %s}".formatted(jwtUtil.createToken(member.getLoginId())));
    }

    @GetMapping("")
    public ResponseEntity<MemberResponse> myInfo(Authentication auth) {
        MemberResponse memberResponse = memberService.getMemberResponseByLoginId(auth.getName());
        return ResponseEntity.ok().body(memberResponse);
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(Authentication auth, @RequestBody @Valid MemberRequest.changePasswordRequest changePasswordRequest) {
        memberService.updateMemberPasswordByLoginId(auth.getName(), changePasswordRequest.getPassword());
        return ResponseEntity.ok().build();
    }

    @PutMapping("")
    public ResponseEntity<MemberResponse> updateInfo(Authentication auth, @RequestBody @Valid MemberRequest memberRequest) {
        MemberResponse memberResponse = memberService.updateMemberInfoByLoginId(auth.getName(), memberRequest);
        return ResponseEntity.ok().body(memberResponse);
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteMember(Authentication auth) {
        memberService.deleteMemberByLoginId(auth.getName());
        return ResponseEntity.ok().body("");
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(exception.getMessage()));
    }
}
