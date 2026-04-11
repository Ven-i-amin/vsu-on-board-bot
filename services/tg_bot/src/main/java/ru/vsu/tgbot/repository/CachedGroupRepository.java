package ru.vsu.tgbot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.tgbot.model.entity.CachedGroup;

@Repository
public interface CachedGroupRepository extends CrudRepository<CachedGroup, String> {
}
