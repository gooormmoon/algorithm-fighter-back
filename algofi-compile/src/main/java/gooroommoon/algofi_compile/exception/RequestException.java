package gooroommoon.algofi_compile.exception;

/**
 * 이 예외가 발생했다는 것은 사용자가 잘못된 요청을 보냈음을 의미하므로 400 Bad Request 반환
 * @see ApiControllerAdvice
 */
public class RequestException extends RuntimeException {
    public RequestException() {
        super();
    }

    public RequestException(String message) {
        super(message);
    }

    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestException(Throwable cause) {
        super(cause);
    }

    protected RequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
