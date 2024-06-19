package gooroommoon.algofi_compile.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CodeExecutionResponse {
    private final String output;
    private final String message;

    public CodeExecutionResponse(String output, String message) {
        this.output = output;
        this.message = message;
    }
}
