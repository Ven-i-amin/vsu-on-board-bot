package ru.vsu.tgbot.services.sessionstate;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.LanguageDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.services.core.LanguageService;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.UiMessage;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class LanguageSessionState implements SessionState {
    public static final int LANGUAGE_ROW_SIZE = 1;
    private final LanguageService languageService;
    private final UiMessageControl uiMessageService;

    private List<LanguageDto> languages = Collections.emptyList();

    @Override
    public void handle(SessionDto sessionDto, BotMessageSender sender) {
        if (sessionDto.getBotState() == BotState.SEND) {
            sender.send(answer(sessionDto));
        } else {
            SendMessage message = listen(sessionDto);

            if (message != null) {
                sender.send(message);
            }
        }
    }


    private SendMessage answer(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.LISTEN);
        List<LanguageDto> availableLanguages = getLanguagesSafely();

        SendMessage.SendMessageBuilder<?, ?> messageBuilder = SendMessage.builder();
        messageBuilder.chatId(sessionDto.getChatId());

        if (availableLanguages.isEmpty()) {
            messageBuilder.text("Language list is temporarily unavailable. Please try again later.");
            return messageBuilder.build();
        }

        String questionText = uiMessageService.getUiMessageText(
                UiMessage.QUESTION_ANSWER,
                sessionDto.getLangCode()
        );
        messageBuilder.text(questionText);

        List<InlineKeyboardRow> languageColumn = MessageUtil.getInlineButtonColumn(
                availableLanguages.stream()
                        .map(LanguageDto::name)
                        .toList(),
                LANGUAGE_ROW_SIZE
        );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(languageColumn);

        messageBuilder.replyMarkup(markup);

        return messageBuilder.build();
    }

    private SendMessage listen(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.SEND);
        List<LanguageDto> availableLanguages = getLanguagesSafely();

        if (availableLanguages.isEmpty()) {
            sessionDto.setBotState(BotState.LISTEN);
            return null;
        }

        LanguageDto language = availableLanguages.stream()
                .filter(lang -> lang.name().equals(sessionDto.getText()))
                .findFirst()
                .orElse(null);

        if (language == null) {
            return null;
        }

        sessionDto.setMessageState(
                sessionDto.getLangCode() == null ? MessageState.MAIN_MENU : MessageState.GROUP
        );

        sessionDto.setLangCode(language.code());


        String questionText = uiMessageService.getUiMessageText(
                UiMessage.QUESTION_LISTEN,
                language.code()
        );

        return SendMessage
                .builder()
                .chatId(sessionDto.getChatId())
                .text(questionText)
                .build();
    }

    @Override
    public MessageState getState() {
        return MessageState.LANGUAGE;
    }

    private List<LanguageDto> getLanguagesSafely() {
        if (!languages.isEmpty()) {
            return languages;
        }

        try {
            languages = languageService.getLanguages();
        } catch (RuntimeException ex) {
            log.warn("Failed to load languages from core service", ex);
            languages = Collections.emptyList();
        }

        return languages;
    }
}
