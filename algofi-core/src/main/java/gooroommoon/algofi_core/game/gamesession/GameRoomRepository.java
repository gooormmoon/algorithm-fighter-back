package gooroommoon.algofi_core.game.gamesession;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
@Getter
public class GameRoomRepository {

    private Map<String, GameRoom> GameRoomMap = new ConcurrentHashMap<>();
    /*home 컨트롤러에서 모델로 사용하기위해서 public static을 붙였습니다.*/
    public static List<GameRoom> gameRooms = new ArrayList<>();

    //save 시에 CreateGameRoomDTO와 memberName을 파라미터로 넘겨주기만 하면 된다.
    public GameRoom save(CreateGameRoomDTO gameRoomDTO) {
        GameRoom gameRoom = GameRoom.of(gameRoomDTO);
        GameRoomMap.put(gameRoom.getGameCode(), gameRoom);
        return gameRoom;
    }

    public Optional<GameRoom> findByGameCode(String gameCode) {
        return Optional.ofNullable(GameRoomMap.get(gameCode));
    }

    //모든 gameRoom 조회
    public List<GameRoom> findAllGameRooms(){
        List<GameRoom> list = new ArrayList<>(GameRoomMap.values());
        return list;
    }

    public void deleteGameRoom(String gameCode) {
        log.info("삭제되었습니다.");
        GameRoomMap.remove(gameCode);
    }

    public int getSize() {
        return GameRoomMap.size();
    }

    public void remove(WebSocketSession session) {
        this.gameRooms.parallelStream().forEach(GameRoom -> GameRoom.remove(session));
    }
}
