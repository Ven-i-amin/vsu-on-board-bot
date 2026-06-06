package ru.vsu.tgbot.repository.local;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.repository.FileStorageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalStorageRepository implements FileStorageRepository {

    public static final int PREFIX_HASH_SIZE = 2;

    private final Path storagePath = Path.of("/storage");

    @Override
    public byte[] load(String fileHash) {
        Path target = resolvePathWithHash(fileHash);

        try {
            return Files.readAllBytes(target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path resolvePathWithHash(String fileHash) {
        return storagePath.resolve(fileHash.substring(0, PREFIX_HASH_SIZE)).resolve(fileHash);
    }
}
