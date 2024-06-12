package gooroommoon.algofi_core.auth.member.dto;

import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.auth.member.MemberRole;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberRequest {
    private String id;
    private String password;
    private String name;
    private String nickname;
    private String profileImageUrl;
    private String description;
}
