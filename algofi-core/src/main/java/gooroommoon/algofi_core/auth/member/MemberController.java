package gooroommoon.algofi_core.auth.member;

import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.MemberResponse;
import gooroommoon.algofi_core.auth.member.dto.TokenResponse;
import gooroommoon.algofi_core.auth.util.JwtUtil;
import gooroommoon.algofi_core.dto.ExceptionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/api/member/register")
    public ResponseEntity<MemberResponse> register(@RequestBody @Valid MemberRequest.RegisterRequest registerRequest) {
        return ResponseEntity.ok().body(memberService.register(registerRequest));
    }

    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicateLoginIdException(DuplicateLoginIdException exception) {
        return ResponseEntity.badRequest().body(new ExceptionResponse(exception.getMessage()));
    }

    @PostMapping("/api/member/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid MemberRequest.LoginRequest loginRequest) {
        return ResponseEntity.ok().body(memberService.authenticate(loginRequest.getId(), loginRequest.getPassword()));
    }

    @GetMapping("/api/member")
    public ResponseEntity<MemberResponse> myInfo(Authentication auth) {
        MemberResponse memberResponse = memberService.getMyInfo(auth.getName());
        return ResponseEntity.ok().body(memberResponse);
    }

    @PutMapping("/api/member/change-password")
    public ResponseEntity<String> changePassword(Authentication auth, @RequestBody @Valid MemberRequest.ChangePasswordRequest changePasswordRequest) {
        memberService.updatePassword(auth.getName(), changePasswordRequest.getPassword());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/member")
    public ResponseEntity<MemberResponse> updateInfo(Authentication auth, @RequestBody @Valid MemberRequest memberRequest) {
        MemberResponse memberResponse = memberService.updateMemberInfo(auth.getName(), memberRequest);
        return ResponseEntity.ok().body(memberResponse);
    }

    @DeleteMapping("/api/member")
    public ResponseEntity<String> deleteMember(Authentication auth, @RequestBody @Valid MemberRequest.LoginRequest deleteRequest) {
        memberService.deleteMember(auth.getName(), deleteRequest.getPassword());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/members/{loginId}")
    public ResponseEntity<MemberResponse> userInfo(@PathVariable String loginId) {
        MemberResponse response = memberService.getInfo(loginId);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUsernameNotFoundException(UsernameNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(exception.getMessage()));
    }
}
