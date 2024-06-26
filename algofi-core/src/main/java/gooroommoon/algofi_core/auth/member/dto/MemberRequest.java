package gooroommoon.algofi_core.auth.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.auth.member.MemberRole;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MemberRequest {
    private String id;
    private String password;
    private String name;
    private String nickname;
    private String profileImageUrl;
    private String description;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    @AllArgsConstructor
    public static class RegisterRequest {
        @NotEmpty(message = "아이디는 필수 항목입니다.")
        private String id;
        @NotEmpty(message = "비밀번호는 필수 항목입니다.")
        private String password;
        @NotEmpty(message = "이름은 필수 항목입니다.")
        private String name;

        private String nickname;
        private String profileImageUrl;
        private String description;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    @AllArgsConstructor
    public static class LoginRequest {
        @NotEmpty(message = "아이디는 필수 항목입니다.")
        private String id;
        @NotEmpty(message = "비밀번호는 필수 항목입니다.")
        private String password;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    @AllArgsConstructor
    public static class ChangePasswordRequest {
        @NotEmpty(message = "비밀번호는 필수 항목입니다.")
        private String password;
    }
}
