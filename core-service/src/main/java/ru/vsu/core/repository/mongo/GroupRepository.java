package ru.vsu.core.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vsu.core.model.entity.Group;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {
    Optional<Group> findByName(String name);

    @Query("{ $or: [ { 'parents': null }, { 'parents': { $size: 0 } } ] }")
    List<Group> findByParentsIsEmpty();

    @Query("{ $expr: { $eq: [ { $arrayElemAt: ['$parents', -1] }, ?0 ] } }")
    List<Group> findDirectChildren(String parentName);

    List<Group> findByParentsContaining(String parentName);

    void deleteByName(String name);

    void deleteByParentsContaining(String parentName);
}