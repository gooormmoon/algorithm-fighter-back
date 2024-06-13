package gooroommoon.algofi_core.auth.member.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenResponse {
    private String accessToken;
}
