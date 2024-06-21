package gooroommoon.algofi_compile.service.language;

import java.nio.file.Path;

public interface CodeExecutor {
    Path makeFileFromCode(String code);

    ProcessBuilder executeProcessBuilder(Path path);

    default void deleteFile(Path path) {
        path.toFile().deleteOnExit();
    }
}
