package ru.vsu.tgbot.services.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.vsu.contract.model.response.UserResponseDto;
import ru.vsu.tgbot.components.mapper.CoreResponseMapper;
import ru.vsu.tgbot.model.dto.UserDto;

@Service
public class UserClientImpl implements UserClient {
    private static final Logger log = LoggerFactory.getLogger(UserClientImpl.class);
    private WebClient webClient;
    private CoreResponseMapper coreResponseMapper;

    public UserClientImpl(WebClient webClient, CoreResponseMapper coreResponseMapper) {
        this.webClient = webClient;
        this.coreResponseMapper = coreResponseMapper;
    }

    @Override
    public UserDto getUser(Long chatId) {
        try {
            return webClient.get()
                    .uri("/bot/user/{chatId}", chatId)
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
                    .uri("/bot/user")
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
                    .uri("/bot/user/{chatId}/langCode", chatId)
                    .bodyValue(new UserResponseDto(chatId, langCode))
                    .retrieve()
                    .bodyToMono(UserResponseDto.class)
                    .block();
        } catch (RuntimeException ex) {
            log.warn("Failed to update langCode for user {}", chatId, ex);
        }
    }
}
