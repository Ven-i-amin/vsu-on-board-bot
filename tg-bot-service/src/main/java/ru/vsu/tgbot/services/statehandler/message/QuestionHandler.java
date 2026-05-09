package ru.vsu.tgbot.services.statehandler.message;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.model.dto.LanguageDto;
import ru.vsu.tgbot.model.dto.QuestionDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.GroupWindowService;
import ru.vsu.tgbot.services.business.QuestionService;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.services.core.LanguageService;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.UiMessageName;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class QuestionHandler implements MessageStateHandler {
    public static final int OTHER_LANGUAGE_ROW_SIZE = 1;
    private final GroupWindowService groupWindowService;
    private final UiMessageControl uiMessageService;
    private final QuestionService questionService;
    private final LanguageService languageService;

    @Override
    public SendMessage answer(SessionDto sessionDto) {
        QuestionDto question = sessionDto.getGroupWindow().getLast().getQuestions().getLast();

        questionService.fixate(question.getName());

        String text = question.getText().getOrDefault(sessionDto.getLangCode(), null);

        if (text == null) {
            sessionDto.setMessageState(MessageState.SELECT_OTHER_LANGUAGE);
            return createOtherLanguageMenu(sessionDto, question);
        }

        return SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(text)
                .replyMarkup(createBackButton(sessionDto))
                .build();
    }

    @Override
    public boolean listen(SessionDto sessionDto) {
        String text = MessageUtil.extractUserInput(sessionDto.getUpdate());

        if (text == null || !text.equals(UiMessageName.BACK.getValue())) {
            return false;
        }

        sessionDto.setMessageState(MessageState.GROUP);
        groupWindowService.removeLastGroup(sessionDto);

        return true;
    }

    @Override
    public MessageState getState() {
        return MessageState.QUESTION;
    }

    private InlineKeyboardMarkup createBackButton(SessionDto sessionDto) {
        List<InlineKeyboardRow> column = MessageUtil.createInlineButtonColumn(
                List.of(
                        uiMessageService.getUiMessageNameAndText(UiMessageName.BACK, sessionDto.getLangCode())
                ),
                1
        );

        return new InlineKeyboardMarkup(column);
    }

    private SendMessage createOtherLanguageMenu(SessionDto sessionDto, QuestionDto question) {
        String otherLanguageMenuText = uiMessageService.getUiMessageText(
                UiMessageName.OTHER_LANGUAGE_MENU,
                sessionDto.getLangCode()
        );

        List<LanguageDto> availableLanguages = languageService.getLanguages();
        if (availableLanguages.isEmpty()) {
            return null;
        }

        Set<String> questionKeys = question.getText().keySet();
        List<Pair<String, String>> questionLanguages = availableLanguages.stream()
                .filter(lang -> questionKeys.contains(lang.code()))
                .map(lang -> Pair.of(lang.code(), lang.name().get(lang.code())))
                .toList();

        if (questionLanguages.isEmpty()) {
            return null;
        }

        List<InlineKeyboardRow> buttonRows = MessageUtil.createInlineButtonColumn(
                questionLanguages,
                OTHER_LANGUAGE_ROW_SIZE
        );

        return SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(otherLanguageMenuText)
                .replyMarkup(new InlineKeyboardMarkup(buttonRows))
                .build();
    }
}
