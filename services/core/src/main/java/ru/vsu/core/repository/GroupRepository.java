package ru.vsu.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.vsu.core.model.entity.Group;

public interface GroupRepository extends MongoRepository<Group, String> {
    java.util.List<Group> findByParentId(String parentId);
    java.util.List<Group> findByParentIdIn(java.util.Collection<String> parentIds);
}
