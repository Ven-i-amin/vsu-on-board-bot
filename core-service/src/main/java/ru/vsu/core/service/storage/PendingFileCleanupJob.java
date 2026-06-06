package ru.vsu.core.service.storage;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.vsu.core.model.entity.QuestionFile;
import ru.vsu.core.repository.FileStorageRepository;
import ru.vsu.core.repository.mongo.QuestionFileRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@AllArgsConstructor
public class PendingFileCleanupJob {

    private static final long PENDING_TTL_MINUTES = 30;

    private final QuestionFileRepository questionFileRepository;
    private final FileStorageRepository fileStorageRepository;

    @Scheduled(fixedDelay = 60_000)
    public void cleanup() {
        Instant threshold = Instant.now().minus(PENDING_TTL_MINUTES, ChronoUnit.MINUTES);
        List<QuestionFile> stale = questionFileRepository
                .findAllByFileUsageLessThanEqualAndUploadedAtBefore(0, threshold);

        for (QuestionFile file : stale) {
            questionFileRepository.deleteByFileHash(file.getFileHash());
            fileStorageRepository.delete(file.getFileHash());
        }
    }
}
