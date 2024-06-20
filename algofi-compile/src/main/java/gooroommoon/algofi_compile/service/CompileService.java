package gooroommoon.algofi_compile.service;

import gooroommoon.algofi_compile.dto.CodeExecutionRequest;
import gooroommoon.algofi_compile.dto.CodeExecutionResponse;
import gooroommoon.algofi_compile.exception.ServerException;
import gooroommoon.algofi_compile.service.language.CodeExecutor;
import gooroommoon.algofi_compile.service.language.Language;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class CompileService {
    private static final long TIME_OUT_MILLIS = 10000;

    public CodeExecutionResponse runCode(CodeExecutionRequest request) {
        Language language = Language.byName(request.getLanguage());

        CodeExecutor codeExecutor = language.getCodeExecutor();

        Path path = codeExecutor.makeFileFromCode(request.getCode());
        ProcessBuilder builder = codeExecutor.executeProcessBuilder(path);
        Process process = startProcess(builder);

        CompletableFuture<StringBuilder> future = readAndGetOutputAsync(request, process);

        StringBuilder output;
        try {
            output = future.get(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            handleTimeOut(process);
            return new CodeExecutionResponse(null, JudgeResult.TIME_LIMIT_EXCEEDED.getMessage());
        } catch (RuntimeException e) {
            return new CodeExecutionResponse(null, JudgeResult.RUNTIME_ERROR.getMessage());
        } catch (ExecutionException | InterruptedException e) {
            throw new ServerException(e);
        }

        int exitCode = getExitCode(process);

        if (exitCode != 0) {
            return new CodeExecutionResponse(null, JudgeResult.RUNTIME_ERROR.getMessage());
        }

        codeExecutor.deleteFile(path);

        String result = output.toString();
        return generateResponse(request, result);
    }

    private Process startProcess(ProcessBuilder builder) {
        builder.redirectErrorStream(true);
        try {
            return builder.start();
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    private CompletableFuture<StringBuilder> readAndGetOutputAsync(CodeExecutionRequest request, Process process) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                transferInput(request.getInput(), process);
                return readOutput(process);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void destroyChildren(Process process) {
        List<ProcessHandle> children = ProcessHandle.allProcesses()
                .filter(processHandle -> processHandle.parent()
                        .map(ProcessHandle::pid)
                        .orElse(-1L) == process.pid())
                .toList();
        children.forEach(ProcessHandle::destroyForcibly);
    }

    private void transferInput(String input, Process process) throws IOException {
        if (input != null) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            writer.write(input);
            writer.newLine();
            writer.flush();
        }
    }

    private StringBuilder readOutput(Process process) throws IOException{
        StringBuilder output = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        return output;
    }

    private void handleTimeOut(Process process) {
        destroyChildren(process);
        process.destroyForcibly();
    }

    private int getExitCode(Process process) {
        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            throw new ServerException(e);
        }
    }

    private CodeExecutionResponse generateResponse(CodeExecutionRequest request, String result) {
        if (!result.equals(request.getExpected())) {
            return new CodeExecutionResponse(result, JudgeResult.WRONG_ANSWER.getMessage());
        }

        return new CodeExecutionResponse(result, JudgeResult.ACCEPTED.getMessage());
    }
}
