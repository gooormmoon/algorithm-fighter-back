package gooroommoon.algofi_compile.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CodeExecutionResponse {
    private final String output;
    private final String expected;
    private final boolean isCorrect;

    public CodeExecutionResponse(String output, String expected, boolean isCorrect) {
        this.output = output;
        this.expected = expected;
        this.isCorrect = isCorrect;
    }
}
