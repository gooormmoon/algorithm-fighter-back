package gooroommoon.algofi_core.auth.member;

import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse register(MemberRequest.RegisterRequest registerRequest) {
        Member member = Member.builder()
                .loginId(registerRequest.getId())
                .password(registerRequest.getPassword())
                .name(registerRequest.getName())
                .nickname(registerRequest.getNickname())
                .profileImageUrl(registerRequest.getProfileImageUrl())
                .description(registerRequest.getDescription())
                .build();
        try {
            memberRepository.save(member);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateLoginIdException("이미 존재하는 아이디입니다.");
        }
        return fromMember(member);
    }

    public Member authenticate(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId);
        if(member != null && member.getPassword().equals(password))
            return member;
        else
            throw new UsernameNotFoundException("아이디나 비밀번호가 틀립니다.");
    }

    public MemberResponse getMemberResponseByLoginId(String loginId) {
        Member member = memberRepository.findByLoginId(loginId);
        if(member != null)
            return fromMember(member);
        else
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
    }

    public void updateMemberPasswordByLoginId(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId);
        if(member != null) {
            member.setPassword(password);
            memberRepository.save(member);
        }
        else
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
    }

    public MemberResponse updateMemberInfoByLoginId(String loginId, MemberRequest memberRequest) {
        Member member = memberRepository.findByLoginId(loginId);


        if(member != null) {
            Optional.ofNullable(memberRequest.getName()).ifPresent(member::setName);
            Optional.ofNullable(memberRequest.getNickname()).ifPresent(member::setNickname);
            Optional.ofNullable(memberRequest.getDescription()).ifPresent(member::setDescription);
            Optional.ofNullable(memberRequest.getProfileImageUrl()).ifPresent(member::setProfileImageUrl);

            memberRepository.save(member);
            return fromMember(member);
        }
        else
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
    }

    public void deleteMemberByLoginId(String loginId) {
        Member member = memberRepository.findByLoginId(loginId);
        if(member != null)
            memberRepository.deleteById(member.getId());
        else
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
    }

    private MemberResponse fromMember(Member member) {
        return MemberResponse.builder()
                .id(member.getLoginId())
                .name(member.getName())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .description(member.getDescription())
                .createdDate(member.getCreatedDate())
                .loginDate(member.getLoginDate())
                .build();
    }

    private Member toMember(MemberRequest memberRequest) {
        return Member.builder()
                .name(memberRequest.getName())
                .nickname(memberRequest.getNickname())
                .loginId(memberRequest.getId())
                .password(memberRequest.getPassword())
                .role(MemberRole.USER)
                .profileImageUrl(memberRequest.getProfileImageUrl())
                .description(memberRequest.getDescription())
                .build();
    }

}
