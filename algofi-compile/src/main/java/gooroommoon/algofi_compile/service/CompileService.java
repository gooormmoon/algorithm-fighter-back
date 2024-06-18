package gooroommoon.algofi_compile.service;

import gooroommoon.algofi_compile.dto.CodeExecutionRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CompileService {

    public String runCode(CodeExecutionRequest request) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder();

        String language = request.getLanguage();
        String code = request.getCode();

        commandFileByLanguage(language, code, builder);
        Process process = startProcess(builder);

        transferInput(request, process);
        StringBuilder output = readOutput(process);

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Execution failed with exit code " + exitCode);
        }

        return output.toString();
    }

    private void commandFileByLanguage(String language, String code, ProcessBuilder builder) throws IOException {
        if (language.equalsIgnoreCase("c")) {
            commandCFile(code, language, builder);
        } else if (language.equalsIgnoreCase("java")) {
            commandJavaFile(code, language, builder);
        } else if (language.equalsIgnoreCase("javascript")) {
            commandJavaScriptFile(code, language, builder);
        } else if (language.equalsIgnoreCase("python")) {
            commandPythonFile(code, language, builder);
        } else {
            throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    private void transferInput(CodeExecutionRequest request, Process process) throws IOException {
        if (request.getInput() != null) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            writer.write(request.getInput());
            writer.newLine();
            writer.flush();
        }
    }

    private Process startProcess(ProcessBuilder builder) throws IOException {
        builder.redirectErrorStream(true);
        return builder.start();
    }

    private StringBuilder readOutput(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output;
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

    private void commandCFile(String code, String language, ProcessBuilder builder) throws IOException {
        Path cFilePath = getTempFilePath(language);
        Files.write(cFilePath, code.getBytes());

        builder.command("sh", "-c", "gcc " + cFilePath + " -o temp && ./temp"); //linux 버전
        //builder.command("cmd.exe", "/c", "gcc " + cFilePath.toString() + " -o temp && temp"); //windows 버전
    }

    private void commandJavaFile(String code, String language, ProcessBuilder builder) throws IOException {
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
        Files.write(javaFilePath, code.getBytes());

        String className = javaFilePath.toString().replace(".java", "");
        //builder.command("cmd.exe", "/c", "javac " + javaFilePath.toString() + " && java " + className); //windows 버전
        builder.command("sh", "-c", "javac " + javaFilePath + " && java " + className); //linux 버전
    }

    private void commandJavaScriptFile(String code, String language, ProcessBuilder builder) throws IOException {
        Path jsFilePath = getTempFilePath(language);
        Files.write(jsFilePath, code.getBytes());

        //builder.command("cmd.exe", "/c", "node " + jsFilePath.toString()); //windows 버전
        builder.command("sh", "-c", "node " + jsFilePath); //linux 버전
    }

    private void commandPythonFile(String code, String language, ProcessBuilder builder) throws IOException {
        Path pyFilePath = getTempFilePath(language);
        Files.write(pyFilePath, code.getBytes());

        //builder.command("cmd.exe", "/c", "python " + pyFilePath.toString()); //windows 버전
        builder.command("sh", "-c", "python3 " + pyFilePath); //linux 버전
    }
}
