package gooroommoon.algofi_compile.controller;

import gooroommoon.algofi_compile.dto.CodeExecutionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CompileApiControllerTest {

    @Autowired
    private CompileApiController compileApiController;

    @Test
    public void testPythonExecution() throws Exception {
        String pythonCode = "print('Hello, Python')";
        ResponseEntity<String> response = compileApiController.compile(new CodeExecutionRequest(pythonCode, "python"));
        assertEquals("Hello, Python\n", response.getBody());
    }

    @Test
    public void testJavaExecution() throws Exception {
        String javaCode = "public class Main { public static void main(String[] args) { System.out.println(\"Hello, Java\"); } }";
        ResponseEntity<String> response = compileApiController.compile(new CodeExecutionRequest(javaCode, "java"));
        assertEquals("Hello, Java\n", response.getBody());
    }

    @Test
    public void testCExecution() throws Exception {
        String cCode = "#include <stdio.h>\nint main() {\nprintf(\"Hello, C\\n\");\nreturn 0;\n}";
        ResponseEntity<String> response = compileApiController.compile(new CodeExecutionRequest(cCode, "c"));
        assertEquals("Hello, C\n", response.getBody());
    }

    @Test
    public void testJavascriptExecution() throws Exception {
        String javascriptCode = "console.log('Hello, JavaScript')";
        ResponseEntity<String> response = compileApiController.compile(new CodeExecutionRequest(javascriptCode, "javascript"));
        assertEquals("Hello, JavaScript\n", response.getBody());
    }
}