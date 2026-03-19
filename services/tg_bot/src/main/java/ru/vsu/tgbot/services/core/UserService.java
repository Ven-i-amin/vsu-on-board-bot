package ru.vsu.tgbot.services.core;

import ru.vsu.tgbot.model.dto.UserDto;

public interface UserService {
    UserDto getUser(Long chatId);
    void addUser(UserDto user);
}
