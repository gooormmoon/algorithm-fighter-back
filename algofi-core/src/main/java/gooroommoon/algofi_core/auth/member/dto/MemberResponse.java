package gooroommoon.algofi_core.auth.member.dto;

import gooroommoon.algofi_core.auth.member.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MemberResponse {
    private String id;
    private String name;
    private String nickname;
    private String profileImageUrl;
    private String description;
    private LocalDateTime createdDate;
    private LocalDateTime loginDate;
}
