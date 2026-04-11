package ru.vsu.tgbot.services.business;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.model.dto.LanguageDto;
import ru.vsu.tgbot.model.dto.UiMessageDto;
import ru.vsu.tgbot.services.core.UiMessageClient;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.UiMessageName;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UiMessageServiceImpl implements UiMessageService {
    private static final long REFRESH_RETRY_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(30);

    private final UiMessageClient uiMessageClient;
    private final LanguageService languageService;

    private volatile List<UiMessageDto> uiMessageList = List.of();
    private volatile long nextRefreshAttemptAt;

    @PostConstruct
    public void init() {
        refreshMessages();
    }

    @Override
    public UiMessageDto getUiMessage(String name) {
        refreshMessagesIfNeeded();
        return uiMessageList.stream()
                .filter(ui -> ui.getName().equals(name))
                .findFirst()
                .orElse(getErrorMessage(name));
    }

    @Override
    public UiMessageDto getUiMessage(UiMessageName name) {
        refreshMessagesIfNeeded();
        return uiMessageList.stream()
                .filter(ui -> ui.getName().equals(name.getValue()))
                .findFirst()
                .orElse(getErrorMessage(name.getValue()));
    }

    @Override
    @NotNull
    public String getUiMessageText(UiMessageName name, String langCode) {
        refreshMessagesIfNeeded();
        UiMessageDto uiMessage = uiMessageList.stream()
                .filter(ui -> ui.getName().equals(name.getValue()))
                .findFirst()
                .orElse(null);

        if (uiMessage == null) {
            return MessageUtil.NOT_FOUND_MESSAGE;
        }

        if (uiMessage.getText() == null || uiMessage.getText().isEmpty()) {
            return MessageUtil.NOT_FOUND_MESSAGE;
        }

        return uiMessage.getText().getOrDefault(
                langCode,
                uiMessage.getText().getOrDefault(MessageUtil.DEFAULT_LANGUAGE_CODE, MessageUtil.NOT_FOUND_MESSAGE)
        );
    }

    @Override
    public Pair<String, String> getUiMessageNameAndText(UiMessageName name, String langCode) {
        refreshMessagesIfNeeded();
        UiMessageDto uiMessage = uiMessageList.stream()
                .filter(ui -> ui.getName().equals(name.getValue()))
                .findFirst()
                .orElse(null);

        if (uiMessage == null) {
            return Pair.of(name.getValue(), MessageUtil.NOT_FOUND_MESSAGE);
        }

        if (uiMessage.getText() == null || uiMessage.getText().isEmpty()) {
            return Pair.of(name.getValue(), MessageUtil.NOT_FOUND_MESSAGE);
        }

        return Pair.of(
                name.getValue(),
                uiMessage.getText().getOrDefault(
                        langCode,
                        uiMessage.getText().getOrDefault(MessageUtil.DEFAULT_LANGUAGE_CODE, MessageUtil.NOT_FOUND_MESSAGE)
                )
        );
    }

    private void refreshMessagesIfNeeded() {
        if (!shouldRefresh()) {
            return;
        }

        synchronized (this) {
            if (!shouldRefresh()) {
                return;
            }
            refreshMessages();
        }
    }

    private boolean shouldRefresh() {
        return (uiMessageList.isEmpty() || isFallbackMessages(uiMessageList))
                && System.currentTimeMillis() >= nextRefreshAttemptAt;
    }

    private boolean isFallbackMessages(List<UiMessageDto> messages) {
        return messages.stream().allMatch(message -> message.getText().size() == 1 && message.getText().containsKey("ru"));
    }

    private void refreshMessages() {
        uiMessageList = uiMessageClient.getUiMessages();
        nextRefreshAttemptAt = System.currentTimeMillis() + REFRESH_RETRY_DELAY_MILLIS;
    }

    private UiMessageDto getErrorMessage(String name) {
        return UiMessageDto.builder()
                .name(name)
                .text(languageService.getLanguages().stream()
                        .map(LanguageDto::code)
                        .collect(Collectors.toMap(
                                Function.identity(),
                                lang -> MessageUtil.NOT_FOUND_MESSAGE)
                        )
                )
                .build();
    }
}
