package ru.vsu.tgbot.services.core;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.vsu.contract.model.response.UserResponseDto;
import ru.vsu.tgbot.components.mapper.CoreResponseMapper;
import ru.vsu.tgbot.model.dto.UserDto;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private WebClient webClient;
    private CoreResponseMapper coreResponseMapper;

    @Override
    public UserDto getUser(Long chatId) {
        try {
            return webClient.get()
                    .uri("/user/{chatId}", chatId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(UserResponseDto.class)
                    .map(coreResponseMapper::toUserDto)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            return null;
        } catch (WebClientResponseException ex) {
            log.warn("Core returned {} while fetching user {}", ex.getStatusCode(), chatId, ex);
            return null;
        } catch (RuntimeException ex) {
            log.warn("Failed to fetch user {} from core", chatId, ex);
            return null;
        }
    }

    @Override
    public void addUser(UserDto user) {
        try {
            webClient.post()
                    .uri("/user")
                    .bodyValue(coreResponseMapper.toUserResponseDto(user))
                    .retrieve()
                    .bodyToMono(UserResponseDto.class)
                    .block();
        } catch (RuntimeException ex) {
            log.warn("Failed to add user {} to core", user.getChatId(), ex);
        }
    }

    @Override
    public void updateLangCode(Long chatId, String langCode) {
        try {
            webClient.put()
                    .uri("/user/{chatId}/langCode", chatId)
                    .bodyValue(new UserResponseDto(chatId, langCode))
                    .retrieve()
                    .bodyToMono(UserResponseDto.class)
                    .block();
        } catch (RuntimeException ex) {
            log.warn("Failed to update langCode for user {}", chatId, ex);
        }
    }
}
