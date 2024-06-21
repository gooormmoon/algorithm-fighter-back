package gooroommoon.algofi_compile.db.presentation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProblemJudgeRequest {
    private final Long algorithmProblemId;
    private final String language;
    private final String code;
}
