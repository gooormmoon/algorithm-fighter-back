package gooroommoon.algofi_core.gameresult;

import gooroommoon.algofi_core.algorithmproblem.Algorithmproblem;
import gooroommoon.algofi_core.auth.member.Member;
import gooroommoon.algofi_core.auth.member.MemberRepository;
import gooroommoon.algofi_core.chat.entity.Chatroom;
import gooroommoon.algofi_core.chat.repository.ChatroomRepository;
import gooroommoon.algofi_core.gameresult.dto.GameresultResponse;
import gooroommoon.algofi_core.gameresult.dto.GameresultsResponse;
import gooroommoon.algofi_core.gameresult.membergameresult.MemberGameresult;
import gooroommoon.algofi_core.gameresult.membergameresult.MemberGameresultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GameresultService {

    private final GameresultRepository gameresultRepository;
    private final MemberGameresultService memberGameresultService;
    private final ChatroomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    /**
     * 멤버의 게임결과 저장하기
     */
    //TODO hostId,guestId,game_over_type,language
    public void save(String chatroomId, Set<String> players ,String hostCode, String guestCode, Algorithmproblem algorithmproblem,
                     int runningTime,String hostId,String hostCodeLanguage, String guestCodeLanguage,String winnerId) {

        Chatroom chatroom = chatRoomRepository.findByChatroomId(chatroomId).orElseThrow(()->
                new IllegalStateException("채팅방을 찾을 수 없습니다."));

        Gameresult gameresult = Gameresult.builder()
                .hostCodeContent(hostCode)
                .guestCodeContent(guestCode)
                .algorithmproblemId(algorithmproblem)
                .chatroomId(chatroom)
                .runningTime(runningTime)
                .hostCodeLanguage(hostCodeLanguage)
                .guestCodeLanguage(guestCodeLanguage)
                .build();

        Gameresult saveGameresult = null;

        //각 멤버 gameresult에 저장
        for (String player : players) {
            Member member = memberRepository.findByLoginId(player)
                    .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

            // Host와 Guest ID 설정
            if (member.getLoginId().equals(hostId)) {
                gameresult.setHostId(member.getLoginId());
            } else {
                gameresult.setGuestId(member.getLoginId());
            }
        }

        //멤버별로 gameresult에 저장
        for (String player : players) {
            Member member = memberRepository.findByLoginId(player)
                    .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

            if (saveGameresult == null) {
                saveGameresult = gameresultRepository.save(gameresult);
            }

            // Game over type 결정
            String gameOverType;
            if (winnerId == null) {
                gameOverType = "TimeOver";
            } else if (winnerId.equals(member.getLoginId())) {
                gameOverType = "WIN";
            } else {
                gameOverType = "LOSE";
            }

            // MemberGameresult 생성 및 저장
            MemberGameresult memberGameresult = MemberGameresult.builder()
                    .member(member)
                    .gameresult(saveGameresult) // saveGameresult 사용
                    .gameOverType(gameOverType)
                    .build();

            memberGameresultService.save(memberGameresult);
        }
    }

    /**
     * 게임 결과 조회하기
     * 멤버의 모든 게임 결과 조회
     */
    public List<GameresultsResponse> findGameresultList(String loginId) {
        List<Gameresult> GameresultsByMemberId = gameresultRepository.findGameresultsByMemberId(loginId);
        return toGameresults(GameresultsByMemberId);
    }

    /**
     * 게임 결과 조회하기
     * 멤버의 특정한 게임 결과 조회
     */
    public GameresultResponse findGameresult(String loginId, Long GameresultId) {
        Gameresult GameresultByMemberIdAndGameresultId = gameresultRepository.findGameresultByMemberIdAndGameresultId(loginId, GameresultId);
        if(GameresultByMemberIdAndGameresultId == null){
            throw new IllegalStateException("게임결과가 없습니다.");
        }else {
            return toGameresult(GameresultByMemberIdAndGameresultId);
        }
    }

    public List<Gameresult> findAllGameresult() {
        return gameresultRepository.findAll();
    }

    //hostId,guestId,hostCodeLanguage,guestCodeLanguage,game_over_type
    private GameresultResponse toGameresult(Gameresult Gameresult) {
        return GameresultResponse.builder()
                .runningTime(Gameresult.getRunningTime())
                .guestCodeContent(Gameresult.getGuestCodeContent())
                .hostCodeContent(Gameresult.getHostCodeContent())
                .hostId(Gameresult.getHostId())
                .guestId(Gameresult.getGuestId())
                .hostCodeLanguage(Gameresult.getHostCodeLanguage())
                .guestCodeLanguage(Gameresult.getGuestCodeLanguage())
                .gameOverType(Gameresult.getGameOverType())
                .title(Gameresult.getAlgorithmproblemId().getTitle())
                .build();
    }

    private List<GameresultsResponse> toGameresults(List<Gameresult> Gameresults) {
        List<GameresultsResponse> results = new CopyOnWriteArrayList<>();
        for (Gameresult Gameresult : Gameresults) {
            GameresultsResponse gameresultsResponse = GameresultsResponse.builder()
                    .title(Gameresult.getAlgorithmproblemId().getTitle())
                    .runningTime(Gameresult.getRunningTime())
                    .gameresultId(Gameresult.getGameresultId())
                    .build();

            results.add(gameresultsResponse);
        }

        return results;
    }
}
