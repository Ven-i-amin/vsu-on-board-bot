package ru.vsu.tgbot.services.core;

import ru.vsu.tgbot.model.response.UserResponseDto;

public interface UserService {
    UserResponseDto getUser(Long chatId);
    void addUser(UserResponseDto user);
}
