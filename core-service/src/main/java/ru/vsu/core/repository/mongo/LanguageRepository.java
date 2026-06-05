package ru.vsu.core.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.core.model.entity.Language;

@Repository
public interface LanguageRepository extends MongoRepository<Language, String> {
}
