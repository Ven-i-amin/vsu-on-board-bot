package ru.vsu.tgbot.services.statehandler.message;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.LanguageDto;
import ru.vsu.tgbot.model.dto.QuestionDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.services.core.LanguageService;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.UiMessageName;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class OtherLanguageHandler implements MessageStateHandler {
    public static final int OTHER_LANGUAGE_ROW_SIZE = 1;
    private LanguageService languageService;
    private UiMessageControl uiMessageControl;

    @Override
    public SendMessage answer(SessionDto sessionDto) {
        String otherLanguageMenuText = uiMessageControl.getUiMessageText(
                UiMessageName.OTHER_LANGUAGE_MENU,
                sessionDto.getLangCode()
        );

        List<LanguageDto> availableLanguages = languageService.getLanguages();

        if (availableLanguages.isEmpty()) {
            return null;
        }

        GroupDto questionGroup = sessionDto.getGroupWindow().getLast();
        QuestionDto question = questionGroup.getQuestions().getLast();
        Set<String> questionKeys = question.getText().keySet();

        List<Pair<String, String>> questionLanguages = getQuestionLanguages(availableLanguages, questionKeys);

        List<InlineKeyboardRow> buttonRows = MessageUtil.createInlineButtonColumn(
                questionLanguages,
                OTHER_LANGUAGE_ROW_SIZE
        );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(buttonRows);

        return SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(otherLanguageMenuText)
                .replyMarkup(markup)
                .build();
    }

    @Override
    public boolean listen(SessionDto sessionDto) {
        List<LanguageDto> availableLanguages = languageService.getLanguages();
        String userInput = MessageUtil.extractUserInput(sessionDto.getUpdate());

        if (availableLanguages.isEmpty() || userInput == null) {
            return false;
        }

        GroupDto questionGroup = sessionDto.getGroupWindow().getLast();
        QuestionDto question = questionGroup.getQuestions().getLast();

        String temporaryText = question.getText().getOrDefault(userInput, null);

        if (temporaryText == null) {
            return false;
        }

        question.setText(Map.of(sessionDto.getLangCode(), temporaryText));
        questionGroup.setQuestions(List.of(question));

        sessionDto.setMessageState(MessageState.QUESTION);
        return true;
    }

    @Override
    public MessageState getState() {
        return null;
    }

    @NotNull
    private static List<Pair<String, String>> getQuestionLanguages(
            List<LanguageDto> languages,
            Set<String> questionKeys
    ) {
        return languages.stream()
                .filter(lang -> questionKeys.contains(lang.code()))
                .map(lang -> Pair.of(lang.code(), lang.name().get(lang.code())))
                .toList();
    }
}
