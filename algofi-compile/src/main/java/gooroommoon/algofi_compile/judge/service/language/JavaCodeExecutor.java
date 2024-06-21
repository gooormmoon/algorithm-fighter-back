package gooroommoon.algofi_compile.judge.service.language;

import gooroommoon.algofi_compile.judge.exception.ServerException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JavaCodeExecutor implements CodeExecutor {
    @Override
    public Path makeFileFromCode(String code) {
        Path path = getPath(code);

        try {
            Files.write(path, code.getBytes());
        } catch (IOException e) {
            throw new ServerException(e);
        }

        return path;
    }

    @Override
    public ProcessBuilder executeProcessBuilder(Path path) {
        String className = path.toString().replace(".java", "");

        return new ProcessBuilder("sh", "-c", "javac " + path + " && java " + className);
    }

    private Path getPath(String code) {
        int classIndex = code.indexOf("public class ");
        if (classIndex == -1) {
            throw new IllegalArgumentException("Invalid Java code: " + code);
        }

        int startIndex = classIndex + "public class ".length();
        int endIndex = code.indexOf(" ", startIndex);
        if (endIndex == -1) {
            throw new IllegalArgumentException("Invalid Java code: " + code);
        }

        String fileName = code.substring(startIndex, endIndex);
        return Paths.get(fileName + ".java");
    }

    @Override
    public void deleteFile(Path path) {
        CodeExecutor.super.deleteFile(path);

        String className = path.toString().replace(".java", ".class");
        try {
            new ProcessBuilder("rm", "-f", className).start();
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }
}
