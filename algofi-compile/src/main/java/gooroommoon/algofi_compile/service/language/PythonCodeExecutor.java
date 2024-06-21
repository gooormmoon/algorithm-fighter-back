package gooroommoon.algofi_compile.service.language;

import gooroommoon.algofi_compile.exception.ServerException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PythonCodeExecutor implements CodeExecutor {
    @Override
    public Path makeFileFromCode(String code) {
        Path path = Paths.get("temp.py");

        try {
            Files.write(path, code.getBytes());
        } catch (IOException e) {
            throw new ServerException(e);
        }

        return path;
    }

    @Override
    public ProcessBuilder executeProcessBuilder(Path path) {
        return new ProcessBuilder("sh", "-c", "python3 " + path);
    }
}
