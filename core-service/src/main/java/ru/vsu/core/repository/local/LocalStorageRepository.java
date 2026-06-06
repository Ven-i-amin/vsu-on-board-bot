package ru.vsu.core.repository.local;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.vsu.core.repository.FileStorageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalStorageRepository implements FileStorageRepository {

    public static final int PREFIX_HASH_SIZE = 2;

    private final Path storagePath = Path.of("/storage");

    @Override
    public String save(String fileHash, byte[] data) {
        Path target = resolvePathWithHash(fileHash);
        Path temp = target.resolveSibling(fileHash + ".tmp");

        try {
            Files.createDirectories(target.getParent());
            Files.write(temp, data);
            Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            try { Files.deleteIfExists(temp); } catch (IOException ignored) {}
            throw new RuntimeException(e);
        }

        return fileHash;
    }

    @Override
    public byte[] load(String fileHash) {
        Path target = resolvePathWithHash(fileHash);

        try {
            return Files.readAllBytes(target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String fileHash) {
        Path target = resolvePathWithHash(fileHash);

        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path resolvePathWithHash(String fileHash) {
        return storagePath.resolve(fileHash.substring(0, PREFIX_HASH_SIZE)).resolve(fileHash);
    }
}
