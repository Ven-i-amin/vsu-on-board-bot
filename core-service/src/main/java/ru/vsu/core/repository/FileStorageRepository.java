package ru.vsu.core.repository;

public interface FileStorageRepository {
    String save(String fileHash, byte[] data);
    byte[] load(String fileHash);
    void delete(String fileHash);
}
