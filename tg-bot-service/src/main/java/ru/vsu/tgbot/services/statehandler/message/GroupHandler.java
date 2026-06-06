package ru.vsu.tgbot.services.statehandler.message;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.QuestionDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.GroupNavigationService;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.services.core.GroupService;
import ru.vsu.tgbot.util.MainMenuState;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.UiMessageName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.vsu.tgbot.util.MessageUtil.NOT_FOUND_MESSAGE;

@Service
@AllArgsConstructor
public class GroupHandler implements MessageStateHandler {
    public static final int GROUP_ROW_SIZE = 1;
    private static final String GROUP_CALLBACK_PREFIX = "g:";
    private static final String QUESTION_CALLBACK_PREFIX = "q:";

    private final GroupNavigationService groupNavigationService;
    private final GroupService groupService;
    private final UiMessageControl uiMessageControl;

    @Override
    public MessageState getState() {
        return MessageState.GROUP;
    }

    @Override
    public SendMessage answer(SessionDto sessionDto) {
        String groupName = groupNavigationService.getCurrentGroupName(sessionDto.getChatId());
        if (groupName == null) {
            sessionDto.setMessageState(MessageState.NOTHING);
            return null;
        }

        GroupDto currentGroup = groupService.getGroupWithContent(groupName);
        Map<String, String> groupTitle = currentGroup.getTitle();
        String translatedTitle = groupTitle.getOrDefault(sessionDto.getLangCode(),
                groupTitle.getOrDefault("ru", NOT_FOUND_MESSAGE));

        List<InlineKeyboardRow> column = MessageUtil.createInlineButtonColumn(
                buildButtonList(currentGroup, sessionDto),
                GROUP_ROW_SIZE
        );

        return SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(translatedTitle)
                .replyMarkup(new InlineKeyboardMarkup(column))
                .build();
    }

    @Override
    public boolean listen(SessionDto sessionDto) {
        String userInput = MessageUtil.extractUserInput(sessionDto.getUpdate());
        String groupName = groupNavigationService.getCurrentGroupName(sessionDto.getChatId());

        if (userInput == null || groupName == null) return false;

        GroupDto currentGroup = groupService.getGroupWithContent(groupName);

        GroupDto selectedGroup = getSelectedGroup(currentGroup, userInput);
        if (selectedGroup != null) {
            groupNavigationService.setCurrentGroup(sessionDto.getChatId(), selectedGroup.getName());
            return true;
        }

        QuestionDto selectedQuestion = getSelectedQuestion(currentGroup, userInput);
        if (selectedQuestion != null) {
            groupNavigationService.setCurrentQuestion(
                    sessionDto.getChatId(), groupName, selectedQuestion.getName());
            sessionDto.setMessageState(MessageState.QUESTION);
            return true;
        }

        if (userInput.equals(UiMessageName.BACK.getValue())) {
            List<String> parents = currentGroup.getParents();
            groupNavigationService.goBack(sessionDto.getChatId(), parents);

            if (groupNavigationService.getCurrentGroupName(sessionDto.getChatId()) == null) {
                sessionDto.setGlobalState(MainMenuState.CREATE);
                sessionDto.setMessageState(MessageState.NOTHING);
            }
            return true;
        }

        return false;
    }

    private List<Pair<String, String>> buildButtonList(GroupDto group, SessionDto sessionDto) {
        List<Pair<String, String>> buttons = new ArrayList<>();
        String langCode = sessionDto.getLangCode();

        for (int i = 0; i < group.getInnerGroups().size(); i++) {
            GroupDto child = group.getInnerGroups().get(i);
            String title = child.getTitle().getOrDefault(langCode, child.getTitle().get("ru"));
            if (title != null) {
                buttons.add(Pair.of(GROUP_CALLBACK_PREFIX + i, title));
            }
        }

        for (int i = 0; i < group.getQuestions().size(); i++) {
            QuestionDto question = group.getQuestions().get(i);
            String title = question.getTitle().getOrDefault(langCode, question.getTitle().get("ru"));
            if (title != null) {
                buttons.add(Pair.of(QUESTION_CALLBACK_PREFIX + i, title));
            }
        }

        buttons.add(uiMessageControl.getUiMessageNameAndText(UiMessageName.BACK, langCode));
        return buttons;
    }

    @Nullable
    private GroupDto getSelectedGroup(GroupDto currentGroup, String userInput) {
        Integer index = parseIndex(userInput, GROUP_CALLBACK_PREFIX);
        if (index == null || index < 0 || index >= currentGroup.getInnerGroups().size()) return null;
        return currentGroup.getInnerGroups().get(index);
    }

    @Nullable
    private QuestionDto getSelectedQuestion(GroupDto currentGroup, String userInput) {
        Integer index = parseIndex(userInput, QUESTION_CALLBACK_PREFIX);
        if (index == null || index < 0 || index >= currentGroup.getQuestions().size()) return null;
        return currentGroup.getQuestions().get(index);
    }

    @Nullable
    private static Integer parseIndex(String userInput, String prefix) {
        if (userInput == null || !userInput.startsWith(prefix)) return null;
        try {
            return Integer.parseInt(userInput.substring(prefix.length()));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
