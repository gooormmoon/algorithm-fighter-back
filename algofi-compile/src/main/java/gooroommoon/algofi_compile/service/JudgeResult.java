package gooroommoon.algofi_compile.service;

import lombok.Getter;

public enum JudgeResult {
    ACCEPTED("맞았습니다."),
    COMPILE_ERROR("컴파일 에러"),
    MEMORY_LIMIT_EXCEEDED("메모리 초과"),
    RUNTIME_ERROR("런타임 에러"),
    TIME_LIMIT_EXCEEDED("시간 초과"),
    WRONG_ANSWER("틀렸습니다.");

    @Getter
    private final String message;

    JudgeResult(String message) {
        this.message = message;
    }
}
