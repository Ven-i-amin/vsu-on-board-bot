package ru.vsu.tgbot.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.tgbot.model.entity.Session;

@Repository
public interface SessionRepository extends CrudRepository<Session, Long> {
}
