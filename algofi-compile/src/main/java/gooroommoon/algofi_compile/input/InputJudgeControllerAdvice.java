package gooroommoon.algofi_compile.input;

import gooroommoon.algofi_compile.input.dto.CodeExecutionResponse;
import gooroommoon.algofi_compile.judge.exception.CodeExecutionException;
import gooroommoon.algofi_compile.judge.exception.RequestException;
import gooroommoon.algofi_compile.judge.exception.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class InputJudgeControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {ServerException.class})
    public ResponseEntity<CodeExecutionResponse> handleServerException(ServerException e) {
        CodeExecutionResponse response = new CodeExecutionResponse(null, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {RequestException.class})
    public ResponseEntity<CodeExecutionResponse> handleRequestException(RequestException e) {
        CodeExecutionResponse response = new CodeExecutionResponse(null, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {CodeExecutionException.class})
    public ResponseEntity<CodeExecutionResponse> handleCodeExecutionException(CodeExecutionException e) {
        CodeExecutionResponse response = new CodeExecutionResponse(null, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
