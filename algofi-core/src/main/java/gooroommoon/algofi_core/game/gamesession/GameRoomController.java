package gooroommoon.algofi_core.game.gamesession;

import gooroommoon.algofi_core.game.exception.GameCreateFailException;
import gooroommoon.algofi_core.game.message.SendSuccessCreateRoom;
import gooroommoon.algofi_core.game.message.SendSuccessDelete;
import gooroommoon.algofi_core.game.message.SendSuccessJoin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameRoomController {

    private final GameRoomService gameRoomService;

    @GetMapping("/rooms")
    public String gameRooms(Model model) {
        List<GameRoom> gameRooms = gameRoomService.findAllRooms();

        model.addAttribute("gameRooms", gameRooms);
        return "gameRooms";
    }

    //게임코드만 view에 넘겨주는 상황
    //algorithm 문제에 대한 것도 넣어줘야함
    @GetMapping("/{gameCode}")
    public String gameSession(@PathVariable("gameCode")String gameCode, Model model) {
        model.addAttribute("gameCode", gameCode);
        return "game";
    }

    //게임 생성
    @PostMapping("")
    public ResponseEntity<SendSuccessCreateRoom<Long>> createGameRoom(@RequestBody CreateGameRoomDTO gameRoomDTO) {
        List<GameRoom> gameRooms = gameRoomService.findAllRooms();
        for (GameRoom gameRoom : gameRooms) {
            if(gameRoomDTO.getHostName().equals(gameRoom.getHostName())){
                throw new GameCreateFailException("유저당 게임 생성은 한개입니다.");
            }
        }
        // Create the game room
        String gameCode = gameRoomService.createGameRoom(gameRoomDTO);
        return ResponseEntity.ok().body(SendSuccessCreateRoom.of(gameCode));
    }

    //게임 삭제
    @DeleteMapping("/{gameCode}")
    public ResponseEntity deleteGameRoom(@RequestBody DeleteGameRoomDTO gameRoomDTO,@PathVariable("gameCode")String gameCode) {
        boolean deleteGame = gameRoomService.CloseGameRoom(gameRoomDTO.getGameCode());
        //memberId가 host
        return ResponseEntity.ok().body(SendSuccessDelete.of(deleteGame));
    }

    //게임 엔터 --> 초대코드를 이용하여 들어가는 방안 생각해야함 웹소켓은 url별로 다르게 설정되어있음
    //api/game/put 시 gameRoom.members 에 추가
    @PutMapping("")
    public ResponseEntity joinMember(@RequestBody JoinGameRoomDTO gameRoomDTO) {
        boolean joinMember = gameRoomService.memberJoinGameRoom(gameRoomDTO.getGameCode(), gameRoomDTO.getHostName());
        if (!joinMember) {
            return ResponseEntity.badRequest().body(SendSuccessJoin.of(false));
        } else {
            //return 제외 코드는 게임방에 들어가있는 멤버 log확인용
            Optional<GameRoom> findGameRoom = gameRoomService.findByGameCode(gameRoomDTO.getGameCode());
            log.info("InGameMembers = {}", findGameRoom.orElseThrow().getMembers());
            return ResponseEntity.ok().body(SendSuccessJoin.of(true));
        }
    }
}
