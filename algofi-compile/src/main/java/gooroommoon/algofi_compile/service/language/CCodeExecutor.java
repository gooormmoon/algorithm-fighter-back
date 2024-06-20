package gooroommoon.algofi_compile.service.language;

import gooroommoon.algofi_compile.exception.ServerException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CCodeExecutor implements CodeExecutor {
    @Override
    public Path makeFileFromCode(String code) {
        Path path = Paths.get("temp.c");

        try {
            Files.write(path, code.getBytes());
        } catch (IOException e) {
            throw new ServerException(e);
        }

        return path;
    }

    @Override
    public ProcessBuilder executeProcessBuilder(Path path) {
        return new ProcessBuilder("sh", "-c", "gcc " + path + " -o temp && ./temp");
    }

    @Override
    public void deleteFile(Path path) {
        CodeExecutor.super.deleteFile(path);

        try {
            new ProcessBuilder("rm", "-f", path.toString().replace(".c", "")).start();
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }
}
