package ru.vsu.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.vsu.core.model.entity.Language;

public interface LanguageRepository extends MongoRepository<Language, String> {
}
