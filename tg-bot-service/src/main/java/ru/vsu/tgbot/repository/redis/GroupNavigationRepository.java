package ru.vsu.tgbot.repository.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vsu.tgbot.model.entity.GroupNavigation;

@Repository
public interface GroupNavigationRepository extends CrudRepository<GroupNavigation, Long> {
}
