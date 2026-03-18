package ru.vsu.tgbot.services.core;

import ru.vsu.tgbot.model.entity.User;

public interface CoreService {
    User getUser(Long chatId);
    void addUser(User user);
}
