package ru.vsu.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.vsu.core.model.entity.User;

public interface UserRepository extends MongoRepository<User, Long> {
}
