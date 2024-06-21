package gooroommoon.algofi_compile.db.presentation.dto;

import lombok.Getter;

@Getter
public class ProblemJudgeResponse {
    private final String message;

    public ProblemJudgeResponse(String message) {
        this.message = message;
    }
}
