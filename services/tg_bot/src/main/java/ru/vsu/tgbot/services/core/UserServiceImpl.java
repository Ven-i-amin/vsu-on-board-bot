package ru.vsu.tgbot.services.core;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.vsu.tgbot.model.response.UserResponseDto;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private WebClient webClient;

    @Override
    public UserResponseDto getUser(Long chatId) {
        return webClient.get()
                .uri("/user/{chatId}", chatId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserResponseDto.class)
                .block();
    }

    @Override
    public void addUser(UserResponseDto user) {
        webClient.post()
                .uri("/user")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(UserResponseDto.class)
                .block();
    }
}
