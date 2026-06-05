package ru.vsu.core.service.storage;

import ru.vsu.core.model.entity.QuestionFile;

import java.io.InputStream;

public interface FileService {
    QuestionFile save(String fileName, InputStream data);
    QuestionFile getAndIncrementUsage(String fileHash);
    QuestionFile getAndDecrementUsage(String fileHash);
}
