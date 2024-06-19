package gooroommoon.algofi_compile.dto;

import gooroommoon.algofi_compile.exception.RequestException;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.StringUtils;

@Getter
@ToString
public class CodeExecutionRequest {
    private final String code;
    private final String language;
    private final String input;
    private final String expected;

    public CodeExecutionRequest(String code, String language, String input, String expected) {
        validatedNotEmptyCode(code);
        validatedNotEmptyLanguage(language);
        validatedNotEmptyExpected(expected);

        this.code = code;
        this.language = language;
        this.input = input;
        this.expected = expected;
    }

    private void validatedNotEmptyCode(String code) {
        if (!StringUtils.hasText(code)) {
            throw new RequestException("Code cannot be empty");
        }
    }

    private void validatedNotEmptyLanguage(String language) {
        if (!StringUtils.hasText(language)) {
            throw new RequestException("Language cannot be empty");
        }
    }

    private void validatedNotEmptyExpected(String expected) {
        if (!StringUtils.hasText(expected)) {
            throw new RequestException("Expected input cannot be empty");
        }
    }
}
