package ru.vsu.core.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.core.model.entity.Question;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
    List<Question> findByGroupName(String groupName);
    Optional<Question> findByGroupNameAndName(String groupName, String name);

    Optional<Question> findByName(String name);

    void deleteByName(String name);

    void deleteByGroupName(String groupName);

    void deleteByGroupNameIn(Collection<String> groupNames);
}