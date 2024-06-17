package gooroommoon.algofi_compile.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class CompileService {

    public String runCode(String code, String language) throws IOException, InterruptedException {
        String fileName = "temp";
        ProcessBuilder builder = new ProcessBuilder();

        if (language.equalsIgnoreCase("c")) {
            commandCFile(code, language, builder);
        } else if (language.equalsIgnoreCase("java")) {
            commandJavaFile(code, language, fileName, builder);
        } else if (language.equalsIgnoreCase("javascript")) {
            commandJavaScriptFile(code, language, builder);
        } else if (language.equalsIgnoreCase("python")) {
            commandPythonFile(code, language, builder);
        } else {
            throw new IllegalArgumentException("Unsupported language: " + language);
        }

        builder.redirectErrorStream(true);
        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Execution failed with exit code " + exitCode);
        }
        return output.toString();
    }

    private void commandCFile(String code, String language, ProcessBuilder builder) throws IOException {
        Path cFilePath = getTempFilePath(language);
        Files.write(cFilePath, code.getBytes());

        builder.command("sh", "-c", "gcc " + cFilePath.toString() + " -o temp && ./temp"); //linux 버전
        //builder.command("cmd.exe", "/c", "gcc " + cFilePath.toString() + " -o temp && temp"); //windows 버전
    }

    private void commandJavaFile(String code, String language, String fileName, ProcessBuilder builder) throws IOException {
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
        builder.command("sh", "-c", "javac " + javaFilePath.toString() + " && java " + className); //linux 버전
    }

    private void commandJavaScriptFile(String code, String language, ProcessBuilder builder) throws IOException {
        Path jsFilePath = getTempFilePath(language);
        Files.write(jsFilePath, code.getBytes());

        //builder.command("cmd.exe", "/c", "node " + jsFilePath.toString()); //windows 버전
        builder.command("sh", "-c", "node " + jsFilePath.toString()); //linux 버전
    }

    private void commandPythonFile(String code, String language, ProcessBuilder builder) throws IOException {
        Path pyFilePath = getTempFilePath(language);
        Files.write(pyFilePath, code.getBytes());

        //builder.command("cmd.exe", "/c", "python " + pyFilePath.toString()); //windows 버전
        builder.command("sh", "-c", "python3 " + pyFilePath.toString()); //linux 버전
    }

    private Path getTempFilePath(String language) {
        return Paths.get("temp." + language.toLowerCase());
    }
}
