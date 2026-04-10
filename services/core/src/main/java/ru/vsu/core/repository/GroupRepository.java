package ru.vsu.core.repository;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ru.vsu.core.model.dto.GroupNodeDto;
import ru.vsu.core.model.entity.Group;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends MongoRepository<Group, String> {
    List<Group> findAllByParentNameIsNull();
    List<Group> findByParentName(String parentName);
    List<Group> findByParentNameIn(java.util.Collection<String> parentNames);

    Optional<Group> findByName(String name);

    void deleteByName(String name);

    void deleteByParentName(String parentName);
}
