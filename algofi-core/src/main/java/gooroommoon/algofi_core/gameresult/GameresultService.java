package hello.proxy.gameresult;

import gooroommoon.algofi_core.algorithmproblem.AlgorithmProblem;
import gooroommoon.algofi_core.algorithmproblem.AlgorithmProblemRepository;
import gooroommoon.algofi_core.algorithmproblem.exception.AlgorithmProblemNotFoundException;
import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.auth.member.MemberRepository;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.repository.ChatRoomRepository;
import gooroommoon.algofi_core.game.session.GameSession;
import gooroommoon.algofi_core.gameresult.dto.GameResultResponse;
import gooroommoon.algofi_core.gameresult.dto.GameResultsResponse;
import gooroommoon.algofi_core.gameresult.membergameresult.MemberGameResult;
import gooroommoon.algofi_core.gameresult.membergameresult.MemberGameResultService;
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
public class GameResultService {

    private final GameResultRepository gameResultRepository;
    private final MemberGameResultService memberGameResultService;
    private final AlgorithmProblemRepository algorithmProblemRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    /**
     * 멤버의 게임결과 저장하기
     */
    //TODO gameSession에서 문제정보와 chatroom정보, 코드 가져오기
    public GameResult save(GameSession session,int runningTime) {

        AlgorithmProblem algorithmProblem = algorithmProblemRepository.findById(1L).orElseThrow(() ->
                new AlgorithmProblemNotFoundException("문제를 찾을 수 없습니다."));

        UUID chatroomId = UUID.fromString("aasdfasdfasdfasdfasdfasdfsadfsda");
        Chatroom chatroom = chatRoomRepository.findByChatroomId(chatroomId).orElseThrow();

        GameResult gameResult = GameResult.builder()
                .hostCodeContent("hostCode")
                .guestCodeContent("guestCode")
                .algorithmProblemId(algorithmProblem)
                .chatroomId(chatroom)
                .runningTime(runningTime)
                .build();

        GameResult saveGameResult = gameResultRepository.save(gameResult);

        Set<String> players = session.getPlayers();
        for (String playerId : players) {
            Member member = memberRepository.findByLoginId(playerId).orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

            MemberGameResult memberGameResult = MemberGameResult.builder()
                    .member(member)
                    .gameResult(gameResult)
                    .build();

            memberGameResultService.save(memberGameResult);
        }

        return saveGameResult;
    }

    /**
     * 게임 결과 조회하기
     * 멤버의 모든 게임 결과 조회
     */
    public List<GameResultsResponse> findGameResultList(String loginId) {
        List<GameResult> gameResultsByMemberId = gameResultRepository.findGameResultsByMemberId(loginId);
        return fromGameResults(gameResultsByMemberId);
    }

    /**
     * 게임 결과 조회하기
     * 멤버의 특정한 게임 결과 조회
     */
    public GameResultResponse findGameResult(String loginId, Long gameResultId) {
        GameResult gameResultByMemberIdAndGameResultId = gameResultRepository.findGameResultByMemberIdAndGameResultId(loginId, gameResultId);
        return fromGameResult(gameResultByMemberIdAndGameResultId);
    }

    private GameResultResponse fromGameResult(GameResult gameResult) {
        return GameResultResponse.builder()
                .runningTime(gameResult.getRunningTime())
                .guestCodeContent(gameResult.getGuestCodeContent())
                .hostCodeContent(gameResult.getHostCodeContent())
                .algorithmProblemId(gameResult.getAlgorithmProblemId())
                .chatroomId(gameResult.getChatroomId())
                .build();
    }

    private List<GameResultsResponse> fromGameResults(List<GameResult> gameResults) {
        List<GameResultsResponse> results = new CopyOnWriteArrayList<>();
        for (GameResult gameResult : gameResults) {
            GameResultsResponse gameResultsResponse = GameResultsResponse.builder()
                    .title(gameResult.getAlgorithmProblemId().getTitle())
                    .runningTime(gameResult.getRunningTime())
                    .build();

            results.add(gameResultsResponse);
        }

        return results;
    }
}
