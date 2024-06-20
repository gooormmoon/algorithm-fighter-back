package gooroommoon.algofi_compile.judge.exception;

import gooroommoon.algofi_compile.input.InputJudgeControllerAdvice;

/**
 * 이 예외가 발생했다는 것은 파일 시스템이나 동시성 문제가 발생했을 수 있으므로 501 Server Error 반환
 * @see InputJudgeControllerAdvice
 */
public class ServerException extends RuntimeException {
    public ServerException() {
        super();
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(Throwable cause) {
        super(cause);
    }

    protected ServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
