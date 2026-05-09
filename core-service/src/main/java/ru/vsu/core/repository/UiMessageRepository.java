package ru.vsu.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.vsu.core.model.entity.UiMessage;

import java.util.Optional;

public interface UiMessageRepository extends MongoRepository<UiMessage, String> {
    boolean existsByName(String name);
    Optional<UiMessage> findByName(String name);
    void deleteByName(String name);
}
