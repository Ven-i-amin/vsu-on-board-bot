package ru.vsu.core.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.core.model.entity.UiMessage;

import java.util.Optional;

@Repository
public interface UiMessageRepository extends MongoRepository<UiMessage, String> {
    boolean existsByName(String name);
    Optional<UiMessage> findByName(String name);
    void deleteByName(String name);
}
