package gooroommoon.algofi_core.gameresult;

import gooroommoon.algofi_core.Algorithmproblem.exception.AlgorithmproblemNotFoundException;
import gooroommoon.algofi_core.algorithmproblem.Algorithmproblem;
import gooroommoon.algofi_core.algorithmproblem.AlgorithmproblemRepository;
import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.auth.member.MemberRepository;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.repository.ChatRoomRepository;
import gooroommoon.algofi_core.game.session.GameSession;
import gooroommoon.algofi_core.gameresult.dto.GameresultResponse;
import gooroommoon.algofi_core.gameresult.dto.GameresultsResponse;
import gooroommoon.algofi_core.gameresult.membergameresult.MemberGameresult;
import gooroommoon.algofi_core.gameresult.membergameresult.MemberGameresultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameresultService {

    private final GameresultRepository gameresultRepository;
    private final MemberGameresultService memberGameresultService;
    private final AlgorithmproblemRepository algorithmproblemRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    /**
     * 멤버의 게임결과 저장하기
     */
    //TODO gameSession에서 문제정보와 chatroom정보, 코드 가져오기
    public Gameresult save(GameSession session,int runningTime) {

        Algorithmproblem algorithmproblem = algorithmproblemRepository.findById(1L).orElseThrow(() ->
                new AlgorithmproblemNotFoundException("문제를 찾을 수 없습니다."));

        UUID chatroomId = UUID.fromString("aasdfasdfasdfasdfasdfasdfsadfsda");
        Chatroom chatroom = chatRoomRepository.findByChatroomId(chatroomId).orElseThrow();

        Gameresult gameresult = Gameresult.builder()
                .hostCodeContent("hostCode")
                .guestCodeContent("guestCode")
                .algorithmproblemId(algorithmproblem)
                .chatroomId(chatroom)
                .runningTime(runningTime)
                .build();

        Gameresult saveGameresult = gameresultRepository.save(gameresult);

        Set<String> players = session.getPlayers();
        for (String playerId : players) {
            Member member = memberRepository.findByLoginId(playerId).orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

            MemberGameresult memberGameresult = MemberGameresult.builder()
                    .member(member)
                    .gameresult(gameresult)
                    .build();

            memberGameresultService.save(memberGameresult);
        }

        return saveGameresult;
    }

    /**
     * 게임 결과 조회하기
     * 멤버의 모든 게임 결과 조회
     */
    public List<GameresultsResponse> findGameresultList(String loginId) {
        List<Gameresult> GameresultsByMemberId = gameresultRepository.findGameresultsByMemberId(loginId);
        return fromGameresults(GameresultsByMemberId);
    }

    /**
     * 게임 결과 조회하기
     * 멤버의 특정한 게임 결과 조회
     */
    public GameresultResponse findGameresult(String loginId, Long GameresultId) {
        Gameresult GameresultByMemberIdAndGameresultId = gameresultRepository.findGameresultByMemberIdAndGameresultId(loginId, GameresultId);
        return fromGameresult(GameresultByMemberIdAndGameresultId);
    }

    private GameresultResponse fromGameresult(Gameresult Gameresult) {
        return GameresultResponse.builder()
                .runningTime(Gameresult.getRunningTime())
                .guestCodeContent(Gameresult.getGuestCodeContent())
                .hostCodeContent(Gameresult.getHostCodeContent())
                .algorithmproblemId(Gameresult.getAlgorithmproblemId().getAlgorithmproblemId())
                .chatroomId(Gameresult.getChatroomId().getChatroomId())
                .build();
    }

    private List<GameresultsResponse> fromGameresults(List<Gameresult> Gameresults) {
        List<GameresultsResponse> results = new CopyOnWriteArrayList<>();
        for (Gameresult Gameresult : Gameresults) {
            GameresultsResponse gameresultsResponse = GameresultsResponse.builder()
                    .title(Gameresult.getAlgorithmproblemId().getTitle())
                    .runningTime(Gameresult.getRunningTime())
                    .build();

            results.add(gameresultsResponse);
        }

        return results;
    }
}
