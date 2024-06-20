package gooroommoon.algofi_core.gameresult.membergameresult;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberGameResultService {

    private final MemberGameResultRepository memberGameResultRepository;

    public MemberGameResult save(MemberGameResult memberGameResult) {
        return memberGameResultRepository.save(memberGameResult);
    }
}
