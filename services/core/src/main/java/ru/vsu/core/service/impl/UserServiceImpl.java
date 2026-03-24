package ru.vsu.core.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.core.component.mapper.UserMapper;
import ru.vsu.core.model.dto.UserDto;
import ru.vsu.core.repository.UserRepository;
import ru.vsu.core.service.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> findAll() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Override
    public UserDto findByChatId(Long chatId) {
        return userRepository.findById(chatId)
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Override
    public UserDto save(UserDto user) {
        return userMapper.toDto(userRepository.save(userMapper.toEntity(user)));
    }

    @Override
    public UserDto updateLangCode(Long chatId, String langCode) {
        UserDto user = findByChatId(chatId);
        if (user == null) {
            user = UserDto.builder()
                    .chatId(chatId)
                    .langCode(langCode)
                    .build();
        } else {
            user.setLangCode(langCode);
        }
        return save(user);
    }

    @Override
    public void deleteByChatId(Long chatId) {
        userRepository.deleteById(chatId);
    }
}
