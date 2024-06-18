package gooroommoon.algofi_compile.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CodeExecutionRequest {
    private final String code;
    private final String language;
    private final String input;
    private final String expected;

    public CodeExecutionRequest(String code, String language, String input, String expected) {
        this.code = code;
        this.language = language;
        this.input = input;
        this.expected = expected;
    }
}
