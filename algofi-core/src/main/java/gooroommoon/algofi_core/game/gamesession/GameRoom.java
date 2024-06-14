package gooroommoon.algofi_core.game.gamesession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class GameRoom {
    private Long id;

    private String gameCode;

    private String hostName;

    private int limitMembers;

    private boolean waiting;

    private static Long sequence = 0L;

    //CopyOnWriteArrayList --> ArrayList보다 멀티쓰레드 환경에서 안정적이다.
    private List<String> members = new CopyOnWriteArrayList<>();

    Set<WebSocketSession> sessions = new HashSet<>();

    public GameJoinMember creatJoinMember() {
        return new GameJoinMember(this.getHostName(), this.getId());
    }

    public GameRoom(CreateGameRoomDTO gameRoomDTO) {
        this.id = ++sequence;
        this.gameCode = UUID.randomUUID().toString().substring(0,5);
        this.hostName = gameRoomDTO.getHostName();
        this.limitMembers = gameRoomDTO.getLimitMembers();
        this.members.add(hostName);
        this.waiting = true;
    }

    public static GameRoom of(CreateGameRoomDTO gameRoomDTO) {
        return new GameRoom(gameRoomDTO);
    }

    public void handleMessage(WebSocketSession session, GameChatMessage chatMessage, ObjectMapper objectMapper) throws JsonProcessingException {
        if (chatMessage.getType().equals("JOIN"))
            join(session);
        else
            send(chatMessage, objectMapper);
    }

    private void join(WebSocketSession session) {
        sessions.add(session);
    }

    private <T> void send(T messageObject, ObjectMapper objectMapper) throws JsonProcessingException {
        TextMessage message = new TextMessage(objectMapper.writeValueAsString(messageObject));

        sessions.parallelStream().forEach(session -> {
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void remove(WebSocketSession target) {
        String targetId = target.getId();
        sessions.removeIf(session -> session.getId().equals(targetId));
    }

    //GameRoom에 Member가 들어올수 있는지 판단하는 로직
    public boolean joinMember(String hostName) {
        if (isNotFullMembers() && !isHost(hostName)) {
            members.add(hostName);
            return true;
        }
        return false;
    }

    //GameRoom에서 host Member가 떠났는지 판단하는 로직
    public boolean leaveMember(Long memberId) {
        if (!isHost(hostName)) {
            int size = members.size();
            members.remove(memberId);

            if (size != members.size()) {
                return true;
            }
        }
        return false;
    }

    //방장인지 확인하는 로직
    public boolean isHost(String hostName) {
        return hostName.equals(this.hostName);
    }

    //GameRoom에 만원인지 확인하는 로직
    private boolean isNotFullMembers() {
        return members.size() < limitMembers;
    }

}

