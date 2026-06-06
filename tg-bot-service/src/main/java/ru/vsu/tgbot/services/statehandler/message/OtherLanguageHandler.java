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
import ru.vsu.tgbot.services.business.GroupNavigationService;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.services.core.GroupService;
import ru.vsu.tgbot.services.core.LanguageService;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.UiMessageName;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class OtherLanguageHandler implements MessageStateHandler {
    public static final int OTHER_LANGUAGE_ROW_SIZE = 1;

    private final GroupNavigationService groupNavigationService;
    private final GroupService groupService;
    private final LanguageService languageService;
    private final UiMessageControl uiMessageControl;

    @Override
    public MessageState getState() {
        return MessageState.SELECT_OTHER_LANGUAGE;
    }

    @Override
    public SendMessage answer(SessionDto sessionDto) {
        String menuText = uiMessageControl.getUiMessageText(
                UiMessageName.OTHER_LANGUAGE_MENU, sessionDto.getLangCode());

        QuestionDto question = resolveCurrentQuestion(sessionDto);
        if (question == null) {
            sessionDto.setMessageState(MessageState.NOTHING);
            return null;
        }

        List<LanguageDto> availableLanguages = languageService.getLanguages();
        if (availableLanguages.isEmpty()) return null;

        Set<String> questionKeys = question.getText().keySet();
        List<Pair<String, String>> questionLanguages = availableLanguages.stream()
                .filter(lang -> questionKeys.contains(lang.code()))
                .map(lang -> Pair.of(lang.code(), lang.name().get(lang.code())))
                .toList();

        if (questionLanguages.isEmpty()) return null;

        List<InlineKeyboardRow> buttonRows = MessageUtil.createInlineButtonColumn(
                questionLanguages, OTHER_LANGUAGE_ROW_SIZE);

        return SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(menuText)
                .replyMarkup(new InlineKeyboardMarkup(buttonRows))
                .build();
    }

    @Override
    public boolean listen(SessionDto sessionDto) {
        String userInput = MessageUtil.extractUserInput(sessionDto.getUpdate());
        if (userInput == null) return false;

        List<LanguageDto> availableLanguages = languageService.getLanguages();
        if (availableLanguages.isEmpty()) return false;

        QuestionDto question = resolveCurrentQuestion(sessionDto);
        if (question == null) return false;

        boolean isValidLang = question.getText().containsKey(userInput);
        if (!isValidLang) return false;

        groupNavigationService.setQuestionOverrideLangCode(sessionDto.getChatId(), userInput);
        sessionDto.setMessageState(MessageState.QUESTION);
        return true;
    }

    private QuestionDto resolveCurrentQuestion(SessionDto sessionDto) {
        String questionGroupName = groupNavigationService.getQuestionGroupName(sessionDto.getChatId());
        String questionName = groupNavigationService.getCurrentQuestionName(sessionDto.getChatId());
        if (questionGroupName == null || questionName == null) return null;

        return groupService.getGroupQuestions(questionGroupName).stream()
                .filter(q -> q.getName().equals(questionName))
                .findFirst()
                .orElse(null);
    }
}
