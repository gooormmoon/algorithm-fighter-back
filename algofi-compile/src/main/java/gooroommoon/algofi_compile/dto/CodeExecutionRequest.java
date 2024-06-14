package gooroommoon.algofi_compile.dto;

public class CodeExecutionRequest {
    private String code; // 실행할 코드
    private String language; // 코드의 프로그래밍 언어

    // 기본 생성자
    public CodeExecutionRequest() {
    }

    // 매개변수가 있는 생성자
    public CodeExecutionRequest(String code, String language) {
        this.code = code;
        this.language = language;
    }

    // code 필드의 getter
    public String getCode() {
        return code;
    }

    // code 필드의 setter
    public void setCode(String code) {
        this.code = code;
    }

    // language 필드의 getter
    public String getLanguage() {
        return language;
    }

    // language 필드의 setter
    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "CodeExecutionRequest{" +
                "code='" + code + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
