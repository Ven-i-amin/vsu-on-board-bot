package ru.vsu.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.vsu.core.model.entity.Admin;

import java.util.Optional;

public interface AdminRepository extends MongoRepository<Admin, String> {
    Optional<Admin> findByEmail(String email);
    boolean existsByEmail(String email);
}
