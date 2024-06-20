package gooroommoon.algofi_core.gameresult;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.auth.member.MemberService;
import gooroommoon.algofi_core.gameresult.membergameresult.MemberGameResult;
import gooroommoon.algofi_core.gameresult.membergameresult.MemberGameResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameResultService {

    private final GameResultRepository gameResultRepository;
    private final MemberGameResultService memberGameResultService;
    private final MemberService memberService;

    /**
     * 멤버의 게임결과 저장하기
     */
    public GameResult save(String jsonData, String loginId) {
        JsonObject asJsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        Long algorithmProblemId = getLongOrNull(asJsonObject, "algorithmProblemId");
        String chatRoomId = getStringOrNull(asJsonObject, "chatroomId");
        String hostCode = getStringOrNull(asJsonObject, "hostCode");
        String guestCode = getStringOrNull(asJsonObject, "guestCode");

        GameResult gameResult = GameResult.builder()
                .hostCodeContent(hostCode)
                .guestCodeContent(guestCode)
                .algorithmProblemId(algorithmProblemId)
                .chatroomId(chatRoomId)
                .build();

        Member member = memberService.getMember(loginId);

        MemberGameResult memberGameResult = MemberGameResult.builder()
                .member(member)
                .gameResult(gameResult)
                .build();

        GameResult saveGameResult = gameResultRepository.save(gameResult);
        memberGameResultService.save(memberGameResult);
        return saveGameResult;
    }

    /**
     * 게임 결과 조회하기
     * 멤버의 모든 게임 결과 조회
     */
    public List<GameResult> findGameResultList(String loginId) {
        List<GameResult> gameResultsByMemberId = gameResultRepository.findGameResultsByMemberId(loginId);
        log.info("gameResultByMemberId = {}", gameResultsByMemberId);
        return gameResultsByMemberId;
    }

    /**
     * 게임 결과 조회하기
     * 멤버의 특정한 게임 결과 조회
     */
    public GameResult findGameResult(String loginId, Long gameResultId) {
        GameResult gameResultByMemberIdAndGameResultId = gameResultRepository.findGameResultByMemberIdAndGameResultId(loginId, gameResultId);
        log.info("gameResultByMemberIdAndGameResultId = {}", gameResultByMemberIdAndGameResultId);
        return gameResultByMemberIdAndGameResultId;
    }

    /**
     * jsonparse LongType
     */
    private static Long getLongOrNull(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        return element != null ? element.getAsLong() : null;
    }

    /**
     * jsonParse StringType
     */
    private static String getStringOrNull(JsonObject jsonObject, String key) {
        JsonElement element = jsonObject.get(key);
        return element != null ? element.getAsString() : null;
    }
}
