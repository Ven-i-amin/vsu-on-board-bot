package ru.vsu.tgbot.repository;

public interface FileStorageRepository {
    byte[] load(String fileHash);
}
