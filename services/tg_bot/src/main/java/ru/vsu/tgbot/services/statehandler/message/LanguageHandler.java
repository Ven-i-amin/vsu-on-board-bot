package ru.vsu.tgbot.services.statehandler.message;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.model.dto.LanguageDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.services.core.LanguageService;
import ru.vsu.tgbot.services.core.UserService;
import ru.vsu.tgbot.util.*;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class LanguageHandler implements MessageStateHandler {
    public static final int LANGUAGE_ROW_SIZE = 1;
    private final LanguageService languageService;
    private final UserService userService;
    private final UiMessageControl uiMessageService;

    @Override
    public SendMessage answer(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.LISTEN);
        List<LanguageDto> availableLanguages = languageService.getLanguages();

        if (availableLanguages.isEmpty()) {
            throw new RuntimeException();
        }

        String questionText = uiMessageService.getUiMessageText(
                UiMessageName.QUESTION_ANSWER,
                sessionDto.getLangCode()
        );

        List<InlineKeyboardRow> languageColumn = getLanguageButtons(availableLanguages);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(languageColumn);

        return SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(questionText)
                .replyMarkup(markup)
                .build();
    }

    @Override
    public boolean listen(SessionDto sessionDto) {
        sessionDto.setGlobalState(MainMenuState.CREATE);

        List<LanguageDto> availableLanguages = languageService.getLanguages();
        String text = MessageUtil.extractUserInput(sessionDto.getUpdate());

        if (availableLanguages.isEmpty() || text == null) {
            return false;
        }

        LanguageDto language = getLanguage(availableLanguages, text);

        if (language == null) {
            return false;
        }

        sessionDto.setLangCode(language.code());
        sessionDto.setMessageState(MessageState.NOTHING);
        userService.updateLangCode(sessionDto.getChatId(), language.code());

        return true;
    }

    @Override
    public MessageState getState() {
        return MessageState.LANGUAGE;
    }

    private LanguageDto getLanguage(List<LanguageDto> availableLanguages, String code) {
        return availableLanguages.stream()
                .filter(lang -> lang.code().equals(code))
                .findFirst()
                .orElse(null);
    }

    private List<InlineKeyboardRow> getLanguageButtons(List<LanguageDto> availableLanguages) {
        return MessageUtil.createInlineButtonColumn(
                availableLanguages.stream()
                        .map(languageDto -> Pair.of(languageDto.code(), languageDto.name()))
                        .toList(),
                LANGUAGE_ROW_SIZE
        );
    }
}
