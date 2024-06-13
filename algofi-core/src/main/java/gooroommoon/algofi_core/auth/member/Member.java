package gooroommoon.algofi_core.auth.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = { @UniqueConstraint(name = "UniqueLoginId", columnNames = "loginId") })
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @NotNull
    @Setter
    private String name;

    @NotNull
    @Setter
    private String nickname;

    @NotNull
    @Setter
    private String loginId;

    @NotNull
    @Setter
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Setter
    private String profileImageUrl;

    @Setter
    private String description;

    @Setter
    private String alert;

    @CreatedDate @NotNull
    @Setter
    private LocalDateTime createdDate;

    @NotNull
    @Setter
    private LocalDateTime loginDate;

    @LastModifiedDate @NotNull
    private LocalDateTime updatedDate;

}
