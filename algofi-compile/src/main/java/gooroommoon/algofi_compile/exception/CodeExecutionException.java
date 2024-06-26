package gooroommoon.algofi_compile.exception;

import gooroommoon.algofi_compile.service.JudgeResult;

/**
 * 이 예외가 발생했다는 것은 시간초과, 런타임 에러 등 제출한 코드에 문제가 있다는 것을 의미한다.
 * 요청 자체에는 문제가 없으므로 200 OK를 반환한다.
 * @see ApiControllerAdvice
 */
public class CodeExecutionException extends RuntimeException {
    private final JudgeResult judgeResult;

    public CodeExecutionException(JudgeResult judgeResult) {
        this.judgeResult = judgeResult;
    }

    @Override
    public String getMessage() {
        return judgeResult.getMessage();
    }
}
