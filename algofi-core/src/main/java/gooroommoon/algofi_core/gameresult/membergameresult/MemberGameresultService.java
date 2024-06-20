package gooroommoon.algofi_core.gameresult.membergameresult;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberGameresultService {

    private final MemberGameresultRepository memberGameresultRepository;

    public MemberGameresult save(MemberGameresult memberGameresult) {
        return memberGameresultRepository.save(memberGameresult);
    }
}
