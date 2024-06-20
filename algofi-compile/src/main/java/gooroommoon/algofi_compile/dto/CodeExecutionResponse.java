package gooroommoon.algofi_compile.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeExecutionResponse {
    private final String output;
    private final String message;

    public CodeExecutionResponse(String output, String message) {
        this.output = output;
        this.message = message;
    }
}
