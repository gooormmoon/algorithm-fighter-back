package gooroommoon.algofi_core.auth.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    private String name;

    private String nickname;

    private String loginId;

    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private String profileImageUrl;

    private String description;

    @CreatedDate
    private LocalDateTime createdDate;

    private LocalDateTime loginDate;

}
