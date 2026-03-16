package ru.vsu.tgbot.services.core;

import ru.vsu.tgbot.model.User;

public interface CoreService {
    User getUser(Long chatId);
    void addUser(User user);
}
