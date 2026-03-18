package ru.vsu.tgbot.services.core;

import org.springframework.stereotype.Service;
import ru.vsu.tgbot.model.entity.User;

@Service
public class CoreServiceImpl implements CoreService {
    @Override
    public User getUser(Long chatId) {
        return null;
    }

    @Override
    public void addUser(User user) {

    }
}
