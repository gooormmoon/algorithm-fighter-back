package gooroommoon.algofi_core.auth.member;

import gooroommoon.algofi_core.auth.member.dto.MemberRequest;
import gooroommoon.algofi_core.auth.member.dto.MemberResponse;
import gooroommoon.algofi_core.auth.member.dto.TokenResponse;
import gooroommoon.algofi_core.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberUserDetailService memberUserDetailService;
    private final JwtUtil jwtUtil;

    public MemberResponse register(MemberRequest.RegisterRequest registerRequest) {
        Member member = Member.builder()
                .loginId(registerRequest.getId())
                .password(registerRequest.getPassword())
                .name(registerRequest.getName())
                .profileImageUrl(registerRequest.getProfileImageUrl())
                .description(registerRequest.getDescription())
                .loginDate(LocalDateTime.now())
                .role(MemberRole.USER)
                .build();

        if(registerRequest.getNickname() == null)
            member.setNickname("user%s".formatted(Math.random()*100));
        else
            member.setNickname(registerRequest.getNickname());

        try {
            memberRepository.save(member);
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateLoginIdException("이미 존재하는 아이디입니다.");
        }
        return fromMember(member);
    }

    public TokenResponse authenticate(String loginId, String password) {
        MemberUserDetails memberDetails = (MemberUserDetails) memberUserDetailService.loadUserByUsername(loginId);

        if(memberDetails.getPassword().equals(password)) {
            memberDetails.getMember().setLoginDate(LocalDateTime.now());
            memberRepository.save(memberDetails.getMember());

            return new TokenResponse(jwtUtil.createToken(memberDetails.getUsername()));
        }
        else {
            throw new UsernameNotFoundException("아이디나 비밀번호가 틀립니다.");
        }
    }

    public MemberResponse getMyInfo(String loginId) {
        Optional<Member> optionalMember = memberRepository.findByLoginId(loginId);
        if(optionalMember.isPresent())
            return fromMember(optionalMember.get());
        else
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
    }

    public MemberResponse getInfo(String loginId) {
        MemberResponse memberResponse = getMyInfo(loginId);
        memberResponse.setLoginDate(null);
        return memberResponse;
    }

    public void updatePassword(String loginId, String password) {
        Optional<Member> optionalMember = memberRepository.findByLoginId(loginId);
        if(optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.setPassword(password);
            memberRepository.save(member);
        }
        else
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
    }

    public MemberResponse updateMemberInfo(String loginId, MemberRequest memberRequest) {
        Optional<Member> optionalMember = memberRepository.findByLoginId(loginId);


        if(optionalMember.isPresent()) {
            Member member = optionalMember.get();

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

    public void deleteMember(String loginId, String password) {
        Optional<Member> optionalMember = memberRepository.findByLoginId(loginId);
        if(optionalMember.isPresent() && optionalMember.get().getPassword().equals(password)) {
            memberRepository.delete(optionalMember.get());
        } else {
            throw new UsernameNotFoundException("아이디나 비밀번호가 틀립니다.");
        }
    }

    public String getMemberNickName(String loginId) {
        Optional<Member> optionalMember = memberRepository.findByLoginId(loginId);
        optionalMember.orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다."));
        return optionalMember.get().getNickname();
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
}
