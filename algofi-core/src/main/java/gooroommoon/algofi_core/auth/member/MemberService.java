package gooroommoon.algofi_core.auth.member;

import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse register(MemberRequest memberRequest) {
        Member member = toMember(memberRequest);
        memberRepository.save(member);
        return fromMember(member);
    }

    public Member authenticate(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId);
        if(member.getPassword().equals(password))
            return member;
        else
            return null;
    }

    public MemberResponse getMemberResponseByLoginId(String loginId) {
        return fromMember(memberRepository.findByLoginId(loginId));
    }

    public void updateMemberPasswordByLoginId(String loginId, String password) {
        Member member = memberRepository.findByLoginId(loginId);
        member.setPassword(password);
        memberRepository.save(member);
    }

    public MemberResponse updateMemberInfoByLoginId(String loginId, MemberRequest memberRequest) {
        Member member = memberRepository.findByLoginId(loginId);
        member.setDescription(memberRequest.getDescription());
        member.setName(memberRequest.getName());
        member.setNickname(memberRequest.getNickname());
        member.setProfileImageUrl(memberRequest.getProfileImageUrl());

        memberRepository.save(member);
        return fromMember(member);
    }

    public void deleteMemberByLoginId(String loginId) {
        Member member = memberRepository.findByLoginId(loginId);
        memberRepository.deleteById(member.getId());
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
