package gooroommoon.algofi_core.game.session.exception;

public class GameIsFullException extends RuntimeException{
    public GameIsFullException(String message) {
        super(message);
    }
}
