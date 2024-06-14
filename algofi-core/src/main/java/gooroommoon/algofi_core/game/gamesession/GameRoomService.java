package gooroommoon.algofi_core.game.gamesession;

import gooroommoon.algofi_core.game.exception.GameRoomNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameRoomService {

    private final GameRoomRepository gameRoomRepository;

    //gameRoom 생성
    public String createGameRoom(CreateGameRoomDTO gameRoomDTO) {
        GameRoom gameRoom = gameRoomRepository.save(gameRoomDTO);
        return gameRoom.getGameCode();
    }

    //gameRoom에 member가 들어갈수있는가?
    public boolean memberJoinGameRoom(String gameCode,String hostName) {
        GameRoom gameRoom = gameRoomRepository.findByGameCode(gameCode).orElseThrow(() -> new GameRoomNotFoundException("찾는 게임방이 없습니다."));
        List<String> members = gameRoom.getMembers();

        //게임방에 같은 멤버가 들어갈 수 없다.
        for (String member : members) {
            if(member.equals(hostName)) {
                log.info("이미 들어간 방입니다.");
                return false;
            }
        }

        //게임방이 가득찼는지 판단
        if(!gameRoom.joinMember(hostName)){
            return false;
        } else{
            gameRoom.joinMember(hostName);
            return true;
        }
    }

    public boolean CloseGameRoom(String gameCode) {
        GameRoom gameRoom = gameRoomRepository.findByGameCode(gameCode).orElseThrow(() -> new GameRoomNotFoundException("찾는 게임방이 없습니다."));
        return false;
    }

    public List<GameRoom> findAllRooms() {
        return gameRoomRepository.findAllGameRooms();
    }

    //멤버 확인용
    public Optional<GameRoom> findByGameCode(String gameCode) {
        return gameRoomRepository.findByGameCode(gameCode);
    }

    public void deleteGameRoom(String gameCode) {
        gameRoomRepository.deleteGameRoom(gameCode);
    }

    public int getSizeGameRoom() {
        return gameRoomRepository.getSize();
    }
}
