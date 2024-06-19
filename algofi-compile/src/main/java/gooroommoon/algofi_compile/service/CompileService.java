package gooroommoon.algofi_compile.service;

import gooroommoon.algofi_compile.dto.CodeExecutionRequest;
import gooroommoon.algofi_compile.dto.CodeExecutionResponse;
import gooroommoon.algofi_compile.exception.RequestException;
import gooroommoon.algofi_compile.exception.ServerException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;

@Service
public class CompileService {
    private static final long TIME_OUT_MILLIS = 10000;

    public CodeExecutionResponse runCode(CodeExecutionRequest request) {
        String language = request.getLanguage();
        String code = request.getCode();

        ProcessBuilder builder = new ProcessBuilder();
        Path path = commandFileByLanguage(language, code, builder);

        Process process = startProcess(builder);

        CompletableFuture<StringBuilder> future = CompletableFuture.supplyAsync(() -> {
            try {
                transferInput(request.getInput(), process);
                return readOutput(process);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        StringBuilder output;
        try {
            output = future.get(TIME_OUT_MILLIS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            List<ProcessHandle> children = ProcessHandle.allProcesses()
                    .filter(processHandle -> processHandle.parent()
                            .map(ProcessHandle::pid)
                            .orElse(-1L) == process.pid())
                    .toList();
            children.forEach(ProcessHandle::destroyForcibly);

            process.destroyForcibly();
            return new CodeExecutionResponse(null, JudgeResult.TIME_LIMIT_EXCEEDED.getMessage());
        } catch (RuntimeException e) {
            return new CodeExecutionResponse(null, JudgeResult.RUNTIME_ERROR.getMessage());
        } catch (ExecutionException | InterruptedException e) {
            throw new ServerException(e);
        }

        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            throw new ServerException(e);
        }

        if (exitCode != 0) {
            return new CodeExecutionResponse(null, JudgeResult.RUNTIME_ERROR.getMessage());
        }

        deleteFile(path);
        if (request.getLanguage().equalsIgnoreCase("java")) {
            String className = path.toString().replace(".java", ".class");
            try {
                new ProcessBuilder("rm", "-f", className).start();
            } catch (IOException e) {
                throw new ServerException(e);
            }
        }

        if (request.getLanguage().equalsIgnoreCase("c")) {
            try {
                new ProcessBuilder("rm", "-f", path.toString().replace(".c", "")).start();
            } catch (IOException e) {
                throw new ServerException(e);
            }
        }

        String result = output.toString();

        if (!result.equals(request.getExpected())) {
            return new CodeExecutionResponse(result, JudgeResult.WRONG_ANSWER.getMessage());
        }

        return new CodeExecutionResponse(result, JudgeResult.ACCEPTED.getMessage());
    }

    private Path commandFileByLanguage(String language, String code, ProcessBuilder builder) {

        if (language.equalsIgnoreCase("c")) {
            return commandCFile(code, language, builder);
        }

        if (language.equalsIgnoreCase("java")) {
            return commandJavaFile(code, language, builder);
        }

        if (language.equalsIgnoreCase("javascript")) {
            return commandJavaScriptFile(code, language, builder);
        }

        if (language.equalsIgnoreCase("python")) {
            return commandPythonFile(code, language, builder);
        }

        throw new RequestException("Unsupported language: " + language);
    }

    private Path commandCFile(String code, String language, ProcessBuilder builder) {
        Path cFilePath = getTempFilePath(language);
        try {
            Files.write(cFilePath, code.getBytes());
        } catch (IOException e) {
            throw new ServerException(e);
        }

        builder.command("sh", "-c", "gcc " + cFilePath + " -o temp && ./temp"); //linux 버전
        //builder.command("cmd.exe", "/c", "gcc " + cFilePath.toString() + " -o temp && temp"); //windows 버전
        return cFilePath;
    }

    private Path commandJavaFile(String code, String language, ProcessBuilder builder) {
        String fileName = "";
        int classIndex = code.indexOf("public class ");
        if (classIndex != -1) {
            int startIndex = classIndex + "public class ".length();
            int endIndex = code.indexOf(" ", startIndex);
            if (endIndex != -1) {
                fileName = code.substring(startIndex, endIndex);
            }
        }

        Path javaFilePath = Paths.get(fileName + "." + language.toLowerCase());
        try {
            Files.write(javaFilePath, code.getBytes());
        } catch (IOException e) {
            throw new ServerException(e);
        }

        String className = javaFilePath.toString().replace(".java", "");
        //builder.command("cmd.exe", "/c", "javac " + javaFilePath.toString() + " && java " + className); //windows 버전
        builder.command("sh", "-c", "javac " + javaFilePath + " && java " + className); //linux 버전
        return javaFilePath;
    }

    private Path commandJavaScriptFile(String code, String language, ProcessBuilder builder) {
        Path jsFilePath = getTempFilePath(language);
        try {
            Files.write(jsFilePath, code.getBytes());
        } catch (IOException e) {
            throw new ServerException(e);
        }

        //builder.command("cmd.exe", "/c", "node " + jsFilePath.toString()); //windows 버전
        builder.command("sh", "-c", "node " + jsFilePath); //linux 버전
        return jsFilePath;
    }

    private Path commandPythonFile(String code, String language, ProcessBuilder builder) {
        Path pyFilePath = getTempFilePath(language);
        try {
            Files.write(pyFilePath, code.getBytes());
        } catch (IOException e) {
            throw new ServerException(e);
        }

        //builder.command("cmd.exe", "/c", "python " + pyFilePath.toString()); //windows 버전
        builder.command("sh", "-c", "python3 " + pyFilePath); //linux 버전
        return pyFilePath;
    }

    private Path getTempFilePath(String language) {
        if (language.equalsIgnoreCase("javascript")) {
            return Paths.get("temp." + "js");
        }
        if (language.equalsIgnoreCase("python")) {
            return Paths.get("temp." + "py");
        }
        return Paths.get("temp." + language.toLowerCase());
    }

    private void transferInput(String input, Process process) throws IOException {
        if (input != null) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            writer.write(input);
            writer.newLine();
            writer.flush();
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

    private StringBuilder readOutput(Process process) throws IOException{
        StringBuilder output = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        return output;
    }

    private void deleteFile(Path path) {
        path.toFile().deleteOnExit();
    }
}
