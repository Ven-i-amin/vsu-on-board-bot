package ru.vsu.contract.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class EnvFileLoader {

    private EnvFileLoader() {
    }

    public static void load(String serviceName) {
        Path workingDirectory = Path.of("").toAbsolutePath().normalize();
        Set<Path> candidates = new LinkedHashSet<>(List.of(
                workingDirectory.resolve(".env"),
                workingDirectory.resolve(".env.local"),
                workingDirectory.resolve(serviceName).resolve(".env"),
                workingDirectory.resolve(serviceName).resolve(".env.local"),
                workingDirectory.getParent() != null ? workingDirectory.getParent().resolve(".env") : workingDirectory.resolve(".env"),
                workingDirectory.getParent() != null ? workingDirectory.getParent().resolve(serviceName).resolve(".env.local") : workingDirectory.resolve(serviceName).resolve(".env.local")
        ));

        for (Path candidate : candidates) {
            loadFile(candidate.normalize());
        }
    }

    private static void loadFile(Path path) {
        if (!Files.isRegularFile(path)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(path)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }

                int separatorIndex = trimmed.indexOf('=');
                if (separatorIndex <= 0) {
                    continue;
                }

                String key = trimmed.substring(0, separatorIndex).trim();
                String value = stripQuotes(trimmed.substring(separatorIndex + 1).trim());

                if (key.isEmpty()) {
                    continue;
                }

                if (System.getenv(key) == null && System.getProperty(key) == null) {
                    System.setProperty(key, value);
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load env file: " + path, exception);
        }
    }

    private static String stripQuotes(String value) {
        if (value.length() >= 2) {
            boolean hasDoubleQuotes = value.startsWith("\"") && value.endsWith("\"");
            boolean hasSingleQuotes = value.startsWith("'") && value.endsWith("'");
            if (hasDoubleQuotes || hasSingleQuotes) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
