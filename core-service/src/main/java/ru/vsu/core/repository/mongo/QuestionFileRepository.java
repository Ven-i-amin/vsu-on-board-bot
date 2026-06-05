package ru.vsu.core.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.core.model.entity.QuestionFile;

import java.time.Instant;
import java.util.List;

@Repository
public interface QuestionFileRepository extends MongoRepository<QuestionFile, String> {
    QuestionFile findByFileHash(String fileHash);
    QuestionFile deleteByFileHash(String fileHash);
    List<QuestionFile> findAllByFileHashIn(List<String> fileHashes);
    List<QuestionFile> findAllByFileUsageLessThanEqualAndUploadedAtBefore(Integer fileUsage, Instant uploadedAt);
}
