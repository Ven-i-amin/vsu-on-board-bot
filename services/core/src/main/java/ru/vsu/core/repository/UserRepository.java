package ru.vsu.core.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.vsu.core.model.entity.User;
import ru.vsu.core.model.response.LanguageCountResponse;

import java.util.List;

public interface UserRepository extends MongoRepository<User, Long> {
    @Aggregation(pipeline = {
            "{ $group: { _id: '$languageCode', count: { $sum: 1 } } }",
            "{ $project: { _id: 0, languageCode: '$_id', count: 1 } }"
    })
    List<LanguageCountResponse> countUserLanguages();
}
