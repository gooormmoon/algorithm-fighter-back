package gooroommoon.algofi_compile.judge.service;

import gooroommoon.algofi_compile.judge.exception.CodeExecutionException;
import gooroommoon.algofi_compile.judge.exception.ServerException;
import gooroommoon.algofi_compile.judge.service.language.CodeExecutor;
import gooroommoon.algofi_compile.judge.service.language.Language;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class JudgeService {
    private static final long TIME_OUT_MILLIS = 10000;

    public CodeExecutor getCodeExecutor(String language) {
        Language lang = Language.byName(language);
        return lang.getCodeExecutor();
    }

    public Process executeCode(CodeExecutor codeExecutor, Path path) {
        ProcessBuilder builder = codeExecutor.executeProcessBuilder(path);
        return startProcess(builder);
    }

    public StringBuilder insertInputAndGetOutput(Process process, String input) {
        CompletableFuture<StringBuilder> future = readAndGetOutputAsync(process, input);

        try {
            StringBuilder output = future.get(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS);
            waitForTerminate(process);
            return output;
        } catch (TimeoutException e) {
            destroy(process);
            throw new CodeExecutionException(JudgeResult.TIME_LIMIT_EXCEEDED);
        } catch (RuntimeException e) {
            throw new CodeExecutionException(JudgeResult.RUNTIME_ERROR);
        } catch (ExecutionException | InterruptedException e) {
            throw new ServerException(e);
        }
    }

    public void waitForTerminate(Process process) {
        int exitCode = waitFor(process);

        if (exitCode != 0) {
            throw new CodeExecutionException(JudgeResult.RUNTIME_ERROR);
        }
    }

    private Process startProcess(ProcessBuilder builder) {
        builder.redirectErrorStream(true);
        try {
            return builder.start();
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    private CompletableFuture<StringBuilder> readAndGetOutputAsync(Process process, String input) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                transferInput(input, process);
                return readOutput(process);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
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

    public void destroy(Process process) {
        destroyChildren(process);
        process.destroyForcibly();
    }

    private void destroyChildren(Process process) {
        List<ProcessHandle> children = ProcessHandle.allProcesses()
                .filter(processHandle -> processHandle.parent()
                        .map(ProcessHandle::pid)
                        .orElse(-1L) == process.pid())
                .toList();
        children.forEach(ProcessHandle::destroyForcibly);
    }

    private int waitFor(Process process) {
        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            throw new ServerException(e);
        }
    }
}