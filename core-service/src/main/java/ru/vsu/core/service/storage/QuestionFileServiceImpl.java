package ru.vsu.core.service.storage;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.vsu.core.model.entity.QuestionFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileStorageService fileStorageService;
    private final MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public QuestionFile save(String fileName, InputStream data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] content;
            try (DigestInputStream dis = new DigestInputStream(data, md)) {
                content = dis.readAllBytes();
            }
            String fileHash = HexFormat.of().formatHex(md.digest());

            Query query = new Query(Criteria.where("fileHash").is(fileHash));
            Update update = new Update().inc("usage", 1).setOnInsert("fileHash", fileHash);

            QuestionFile questionFile = mongoTemplate.findAndModify(
                    query,
                    update,
                    FindAndModifyOptions.options().upsert(true).returnNew(true),
                    QuestionFile.class
            );

            if (Objects.requireNonNull(questionFile).getFileUsage() == 1) {
                fileStorageService.save(fileHash, content);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
                            fileStorageService.delete(fileHash);
                        }
                    }
                });
            }

            return questionFile;
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public QuestionFile getAndIncrementUsage(String fileHash) {
        Query query = new Query().addCriteria(Criteria.where("fileHash").is(fileHash));
        Update update = new Update().inc("usage");

        return mongoTemplate.findAndModify(query, update, QuestionFile.class);
    }

    @Override
    public QuestionFile getAndDecrementUsage(String fileHash) {
        Query query = new Query().addCriteria(Criteria.where("fileHash").is(fileHash));
        Update update = new Update().inc("usage", -1);

        return mongoTemplate.findAndModify(query, update, QuestionFile.class);
    }
}
