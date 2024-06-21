package gooroommoon.algofi_compile.judge.service.language;

import gooroommoon.algofi_compile.judge.exception.CodeExecutionException;
import gooroommoon.algofi_compile.judge.exception.ServerException;
import gooroommoon.algofi_compile.judge.service.JudgeResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JavaCodeExecutor implements CodeExecutor {

    private final List<String> classNames = new ArrayList<>();

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

    @Override
    public void deleteFile(Path path) {
        CodeExecutor.super.deleteFile(path);

        classNames.forEach(className -> {
            try {
                new ProcessBuilder("rm", "-f", className + ".class").start();
            } catch (IOException e) {
                throw new ServerException(e);
            }
        }
        );
    }

    private Path getPath(String code) {
        addClassNames(code);
        int classIndex = code.indexOf("public class ");
        if (classIndex == -1) {
            throw new CodeExecutionException(JudgeResult.COMPILE_ERROR);
        }

        int startIndex = classIndex + "public class ".length();
        int endIndex = code.indexOf(" ", startIndex);
        if (endIndex == -1) {
            throw new CodeExecutionException(JudgeResult.COMPILE_ERROR);
        }

        String fileName = code.substring(startIndex, endIndex);
        if (fileName.contains("{")) {
            int index = fileName.indexOf("{");
            fileName = fileName.substring(0, index);
        }

        return Paths.get(fileName + ".java");
    }

    private void addClassNames(String code) {
        String target = "class ";
        int classIndex = code.indexOf(target);

        while (classIndex >= 0) {
            int startIndex = classIndex + target.length();
            int endIndex = code.indexOf(" ", startIndex);

            if (endIndex == -1) {
                break;
            }

            String fileName = code.substring(startIndex, endIndex);
            if (fileName.contains("{")) {
                int index = fileName.indexOf("{");
                fileName = fileName.substring(0, index);
            }

            classNames.add(fileName);
            classIndex = code.indexOf(target, endIndex + 1);
        }
    }
}
