package gooroommoon.algofi_compile.service.language;

import gooroommoon.algofi_compile.exception.RequestException;

import java.util.Arrays;

public enum Language {
    C(new CCodeExecutor()),
    JAVA(new JavaCodeExecutor()),
    PYTHON(new PythonCodeExecutor()),
    JAVASCRIPT(new JavaScriptCodeExecutor());

    private final CodeExecutor codeExecutor;

    Language(CodeExecutor codeExecutor) {
        this.codeExecutor = codeExecutor;
    }

    public static Language byName(String name) {
        return Arrays.stream(Language.values())
                .filter(lang -> lang.name().equalsIgnoreCase(name))
                .findFirst().orElseThrow(() -> new RequestException("Unsupported language: " + name));
    }

    public CodeExecutor getCodeExecutor() {
        return codeExecutor;
    }
}
