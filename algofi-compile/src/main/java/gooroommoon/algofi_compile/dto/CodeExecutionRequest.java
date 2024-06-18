package gooroommoon.algofi_compile.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class CodeExecutionRequest {
    private String code;
    private String language;
    private String input;
    private String expected;

    public CodeExecutionRequest(String code, String language, String input, String expected) {
        this.code = code;
        this.language = language;
        this.input = input;
        this.expected = expected;
    }
}
