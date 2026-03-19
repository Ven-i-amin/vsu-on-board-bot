package ru.vsu.tgbot.services.core;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.tgbot.components.mapper.CoreResponseMapper;
import ru.vsu.tgbot.model.dto.UserDto;
import ru.vsu.tgbot.model.response.UserResponseDto;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private WebClient webClient;
    private CoreResponseMapper coreResponseMapper;

    @Override
    public UserDto getUser(Long chatId) {
        return webClient.get()
                .uri("/user/{chatId}", chatId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserResponseDto.class)
                .map(coreResponseMapper::toUserDto)
                .block();
    }

    @Override
    public void addUser(UserDto user) {
        webClient.post()
                .uri("/user")
                .bodyValue(coreResponseMapper.toUserResponseDto(user))
                .retrieve()
                .bodyToMono(UserResponseDto.class)
                .block();
    }
}
