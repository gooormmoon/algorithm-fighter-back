package gooroommoon.algofi_core.auth.member.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import gooroommoon.algofi_core.auth.member.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MemberResponse {
    private String id;
    private String name;
    private String nickname;
    private String profileImageUrl;
    private String description;
    private LocalDateTime createdDate;
    @Setter
    private LocalDateTime loginDate;
}
