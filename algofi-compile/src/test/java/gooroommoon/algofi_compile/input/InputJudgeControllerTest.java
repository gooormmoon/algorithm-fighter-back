package gooroommoon.algofi_compile.input;

import gooroommoon.algofi_compile.input.dto.CodeExecutionRequest;
import gooroommoon.algofi_compile.input.dto.CodeExecutionResponse;
import gooroommoon.algofi_compile.judge.exception.CodeExecutionException;
import gooroommoon.algofi_compile.judge.service.JudgeResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class InputJudgeControllerTest {

    @Autowired
    private InputJudgeController inputJudgeController;

    @Test
    public void testPythonExecution() throws IOException {
        File file = new File("src/test/resources/fixture/Hello.py");
        String pythonCode = fileToString(file);
        CodeExecutionRequest request = new CodeExecutionRequest(pythonCode, "python", null, "Hello, Python\n");

        ResponseEntity<CodeExecutionResponse> response = inputJudgeController.judgeInput(request);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(JudgeResult.ACCEPTED.getMessage());
    }

    @Test
    public void testJavascriptExecution() throws IOException {
        File file = new File("src/test/resources/fixture/Hello.js");
        String javascriptCode = fileToString(file);
        CodeExecutionRequest request = new CodeExecutionRequest(javascriptCode, "javascript", null, "Hello, JavaScript\n");

        ResponseEntity<CodeExecutionResponse> response = inputJudgeController.judgeInput(request);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(JudgeResult.ACCEPTED.getMessage());
    }

    @Test
    public void testCExecution() throws IOException {
        File file = new File("src/test/resources/fixture/Hello.c");
        String cCode = fileToString(file);
        CodeExecutionRequest request = new CodeExecutionRequest(cCode, "c", null, "Hello, C\n");

        ResponseEntity<CodeExecutionResponse> response = inputJudgeController.judgeInput(request);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(JudgeResult.ACCEPTED.getMessage());
    }

    @Test
    public void testJavaExecution() throws IOException {
        File file = new File("src/test/resources/fixture/Hello.java");
        String javaCode = fileToString(file);
        CodeExecutionRequest request = new CodeExecutionRequest(javaCode, "java", null, "Hello, Java\n");

        ResponseEntity<CodeExecutionResponse> response = inputJudgeController.judgeInput(request);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(JudgeResult.ACCEPTED.getMessage());
    }

    @Test
    public void testPythonExecutionWithParam() throws IOException {
        File file = new File("src/test/resources/fixture/PrintInput.py");
        String pythonCode = fileToString(file);
        CodeExecutionRequest request = new CodeExecutionRequest(pythonCode, "python", "Hello, python\n", "Hello, python\n");

        ResponseEntity<CodeExecutionResponse> response = inputJudgeController.judgeInput(request);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(JudgeResult.ACCEPTED.getMessage());
    }

    @Test
    public void testJavascriptExecutionWithParam() throws IOException {
        File file = new File("src/test/resources/fixture/PrintInput.js");
        String javascriptCode = fileToString(file);
        CodeExecutionRequest request = new CodeExecutionRequest(javascriptCode, "javascript", "Hello, JavaScript\n", "Hello, JavaScript\n");

        ResponseEntity<CodeExecutionResponse> response = inputJudgeController.judgeInput(request);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(JudgeResult.ACCEPTED.getMessage());
    }


    @Test
    public void testCExecutionWithParam() throws IOException {
        File file = new File("src/test/resources/fixture/PrintInput.c");
        String cCode = fileToString(file);
        CodeExecutionRequest request = new CodeExecutionRequest(cCode, "c", "Hello, C\n", "Hello, C\n");

        ResponseEntity<CodeExecutionResponse> response = inputJudgeController.judgeInput(request);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(JudgeResult.ACCEPTED.getMessage());
    }

    @Test
    public void testJavaExecutionWithParam() throws IOException {
        File file = new File("src/test/resources/fixture/PrintInput.java");
        String javaCode = fileToString(file);
        CodeExecutionRequest request = new CodeExecutionRequest(javaCode, "java", "Hello, Java\n", "Hello, Java\n");

        ResponseEntity<CodeExecutionResponse> response = inputJudgeController.judgeInput(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(JudgeResult.ACCEPTED.getMessage());
    }

    @Test
    public void timeoutTest() throws IOException {
        File file = new File("src/test/resources/fixture/While.java");
        String code = fileToString(file);
        CodeExecutionRequest request = new CodeExecutionRequest(code, "java", null, "fail");

        assertThatThrownBy(() -> inputJudgeController.judgeInput(request))
                .isInstanceOf(CodeExecutionException.class)
                .hasMessageContaining(JudgeResult.TIME_LIMIT_EXCEEDED.getMessage());
    }

    @Test
    public void wrongAnswerPy() throws IOException {
        File file = new File("src/test/resources/fixture/Hello.py");
        String pythonCode = fileToString(file);
        CodeExecutionRequest request = new CodeExecutionRequest(pythonCode, "python", null, "Hello, C\n");

        assertThatThrownBy(() -> inputJudgeController.judgeInput(request))
                .isInstanceOf(CodeExecutionException.class)
                .hasMessageContaining(JudgeResult.WRONG_ANSWER.getMessage());
    }

    @Test
    public void anotherClass() throws IOException {
        File file = new File("src/test/resources/fixture/Napsack.java");
        String code = fileToString(file);
        CodeExecutionRequest request = new CodeExecutionRequest(code, "java", "5 20 10 5 25 12 15 8 6 3 7 4", "41");

        ResponseEntity<CodeExecutionResponse> response = inputJudgeController.judgeInput(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(JudgeResult.ACCEPTED.getMessage());
    }

    @Test
    public void withLeftBrace() throws IOException {
        File file = new File("src/test/resources/fixture/NapsackBrace.java");
        String code = fileToString(file);
        CodeExecutionRequest request = new CodeExecutionRequest(code, "java", "5 20 10 5 25 12 15 8 6 3 7 4", "41");

        ResponseEntity<CodeExecutionResponse> response = inputJudgeController.judgeInput(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(Objects.requireNonNull(response.getBody()).getMessage()).isEqualTo(JudgeResult.ACCEPTED.getMessage());
    }

    private String fileToString(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}