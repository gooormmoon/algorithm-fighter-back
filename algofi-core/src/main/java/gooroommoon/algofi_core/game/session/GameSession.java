package gooroommoon.algofi_core.game.session;

import gooroommoon.algofi_core.game.session.exception.GameIsFullException;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

@Getter
public class GameSession {

    private String title;

    private final String hostId;

    private final Set<String> players;

    private final Set<String> readyPlayers;

    private int maxPlayer;

    private String problemLevel;

    private int timerTime;

    @Setter
    private String hostGameCode;
    @Setter
    private String otherGameCode;

    //TODO 알고리즘 문제 세션에 저장
    // private AlgorithmProblem algorithmProblem;

    private boolean isStarted;

    private long startTime;

    private final String chatroomId;

    @Setter
    private ScheduledFuture<?> timeOverTask;

    protected GameSession(String hostId, String title, String problemLevel, Integer timerTime) {
        this.hostId = hostId;
        this.maxPlayer = 2;
        this.players = new CopyOnWriteArraySet<>();
        players.add(hostId);
        this.readyPlayers = new CopyOnWriteArraySet<>();
        this.problemLevel = "1";
        this.timerTime = 1200;
        this.isStarted = false;
        this.chatroomId = UUID.randomUUID().toString();
        updateSettings(title, problemLevel, timerTime);
    }

    public Stream<String> getPlayersStream() {
        return players.stream();
    }

    public boolean isFull() {
        return players.size() >= maxPlayer;
    }

    protected void addPlayer(String playerId) {
        if(isFull()) {
            throw new GameIsFullException("게임이 꽉 찼습니다.");
        } else {
            players.add(playerId);
        }
    }

    protected void addReadyPlayer(String loginId) {
        if(players.contains(loginId)) {
            readyPlayers.add(loginId);
        }
    }

    protected boolean allReady() {
        return players.size() <= readyPlayers.size() + 1;
    }


    public void removePlayer(String playerId) {
        players.remove(playerId);
        readyPlayers.remove(playerId);
    }

    protected void updateSettings(String title, String problemLevel, Integer timerTime) {
        if(title != null) {
            this.title = title;
        }
        if(problemLevel != null) {
            this.problemLevel = problemLevel;
        }
        if(timerTime != null) {
            this.timerTime = timerTime;
        }

    }

    protected void start() {
        //TODO 알고리즘 문제 받아오기
        isStarted = true;
        startTime = System.currentTimeMillis();
    }
}
