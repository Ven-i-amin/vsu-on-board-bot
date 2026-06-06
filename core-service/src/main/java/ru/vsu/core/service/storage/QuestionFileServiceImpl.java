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
import ru.vsu.core.component.mapper.QuestionFileMapper;
import ru.vsu.core.model.dto.QuestionFileDto;
import ru.vsu.core.model.entity.QuestionFile;
import ru.vsu.core.repository.FileStorageRepository;
import ru.vsu.core.repository.mongo.QuestionFileRepository;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class QuestionFileServiceImpl implements QuestionFileService {
    private final QuestionFileRepository questionFileRepository;
    private final FileStorageRepository fileStorageRepository;
    private final MongoTemplate mongoTemplate;
    private final QuestionFileMapper questionFileMapper;

    @Override
    @Transactional
    public QuestionFileDto save(String fileName, InputStream data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] content;
            try (DigestInputStream dis = new DigestInputStream(data, md)) {
                content = dis.readAllBytes();
            }
            String fileHash = HexFormat.of().formatHex(md.digest());

            Query query = new Query(Criteria.where("fileHash").is(fileHash));
            Update update = new Update()
                    .setOnInsert("fileHash", fileHash)
                    .setOnInsert("fileName", fileName)
                    .setOnInsert("fileUsage", 0)
                    .setOnInsert("uploadedAt", Instant.now());

            QuestionFile questionFile = mongoTemplate.findAndModify(
                    query,
                    update,
                    FindAndModifyOptions.options().upsert(true).returnNew(true),
                    QuestionFile.class
            );

            if (Objects.requireNonNull(questionFile).getFileUsage() != null
                    && questionFile.getFileUsage() == 1) {
                fileStorageRepository.save(fileHash, content);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
                            fileStorageRepository.delete(fileHash);
                        }
                    }
                });
            }

            return questionFileMapper.toDto(questionFile);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public QuestionFileDto get(String fileHash) {
        return questionFileMapper.toDto(questionFileRepository.findByFileHash(fileHash));
    }

    @Override
    public List<QuestionFileDto> getAll(List<String> fileHashes) {
        return questionFileRepository.findAllByFileHashIn(fileHashes).stream()
                .map(questionFileMapper::toDto)
                .toList();
    }

    @Override
    public byte[] getFile(String fileHash) {
        return fileStorageRepository.load(fileHash);
    }

    @Override
    public boolean incrementUsage(String fileHash) {
        Query query = new Query().addCriteria(Criteria.where("fileHash").is(fileHash));
        Update update = new Update().inc("usage");

        QuestionFile questionFile = mongoTemplate.findAndModify(
                query,
                update,
                QuestionFile.class
        );

        return questionFile != null;
    }

    @Override
    public boolean incrementUsage(List<String> fileHashes) {
        Query query = new Query().addCriteria(Criteria.where("fileHash").in(fileHashes));
        Update update = new Update().inc("usage");

        Long matchedCount = mongoTemplate.updateMulti(query, update, QuestionFile.class).getMatchedCount();

        return isAllFilesUpdated(fileHashes, matchedCount);
    }

    @Override
    @Transactional
    public boolean decrementUsage(String fileHash) {
        Query query = new Query().addCriteria(Criteria.where("fileHash").is(fileHash));
        Update update = new Update().inc("usage", -1);

        QuestionFile questionFile = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                QuestionFile.class
        );

        if (questionFile == null) {
            return false;
        }

        if (questionFile.getFileUsage() < 1) {
            delete(fileHash);
        }

        return true;
    }

    @Override
    public boolean decrementUsage(List<String> fileHashes) {
        Query query = new Query().addCriteria(Criteria.where("fileHash").in(fileHashes));
        Update update = new Update().inc("usage", -1);

        long matchedCount = mongoTemplate.updateMulti(query, update, QuestionFile.class).getMatchedCount();

        return isAllFilesUpdated(fileHashes, matchedCount);
    }

    @Override
    @Transactional
    public QuestionFileDto delete(String fileHash) {
        QuestionFile questionFile = questionFileRepository.deleteByFileHash(fileHash);

        if (questionFile == null) {
            throw new RuntimeException();
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                fileStorageRepository.delete(fileHash);
            }
        });

        return questionFileMapper.toDto(questionFile);
    }


    private static boolean isAllFilesUpdated(List<String> fileHashes, Long matchedCount) {
        return matchedCount == fileHashes.size();
    }
}
