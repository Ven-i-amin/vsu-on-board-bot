package ru.vsu.core.service.storage;

import ru.vsu.core.model.dto.QuestionFileDto;

import java.io.InputStream;
import java.util.List;

public interface QuestionFileService {
    QuestionFileDto save(String fileName, InputStream data);

    QuestionFileDto get(String fileHash);

    List<QuestionFileDto> getAll(List<String> fileHashes);

    byte[] getFile(String fileHash);

    boolean incrementUsage(String fileHash);

    boolean incrementUsage(List<String> fileHashes);

    boolean decrementUsage(String fileHash);

    boolean decrementUsage(List<String> fileHashes);

    QuestionFileDto delete(String fileHash);
}
