package ru.vsu.core.service;

import ru.vsu.core.model.dto.UserDto;
import ru.vsu.core.model.response.LanguageCountResponse;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();
    UserDto findByChatId(Long chatId);
    UserDto save(UserDto user);

    List<LanguageCountResponse> getUserLanguageUsage();

    UserDto updateLangCode(Long chatId, String langCode);
    void deleteByChatId(Long chatId);
}
