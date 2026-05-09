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
import ru.vsu.tgbot.services.business.GroupWindowService;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.UiMessageName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.vsu.tgbot.util.MessageUtil.NOT_FOUND_MESSAGE;
import static ru.vsu.tgbot.util.MessageUtil.createGroupForQuestion;

@Service
@AllArgsConstructor
public class GroupHandler implements MessageStateHandler {
    public static final int GROUP_ROW_SIZE = 1;
    private static final String GROUP_CALLBACK_PREFIX = "g:";
    private static final String QUESTION_CALLBACK_PREFIX = "q:";
    private final GroupWindowService groupWindowService;
    private final UiMessageControl uiMessageService;

    @Override
    public MessageState getState() {
        return MessageState.GROUP;
    }

    @Override
    public SendMessage answer(SessionDto sessionDto) {
        List<GroupDto> groupWindow = sessionDto.getGroupWindow();

        if (groupWindow.isEmpty()) {
            sessionDto.setMessageState(MessageState.NOTHING);

            return null;
        }

        Map<String, String> groupTitle = groupWindow.getLast().getTitle();

        String translatedTitle = groupTitle.getOrDefault(sessionDto.getLangCode(), groupTitle.getOrDefault("ru", NOT_FOUND_MESSAGE));

        List<InlineKeyboardRow> column = MessageUtil.createInlineButtonColumn(
                getAllTitles(sessionDto),
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
        List<GroupDto> groupWindow = sessionDto.getGroupWindow();

        if (userInput == null || groupWindow.isEmpty()) {
            return false;
        }

        GroupDto currentGroup = groupWindow.getLast();
        GroupDto selectedGroup = getSelectedGroup(currentGroup, userInput);

        if (selectedGroup != null) {
            groupWindowService.moveForward(sessionDto, selectedGroup);
            return true;
        }

        QuestionDto selectedQuestion = getSelectedQuestion(currentGroup, userInput);

        if (selectedQuestion != null) {
            GroupDto questionGroup = createGroupForQuestion(selectedQuestion);

            groupWindowService.moveForward(sessionDto, questionGroup);
            sessionDto.setMessageState(MessageState.QUESTION);
            return true;
        }

        if (userInput.equals(UiMessageName.BACK.getValue())) {
            groupWindowService.moveBackward(sessionDto);
            return true;
        }

        return false;
    }

    private List<Pair<String, String>> getAllTitles(SessionDto sessionDto) {
        GroupDto currentGroup = sessionDto.getGroupWindow().getLast();
        List<Pair<String, String>> nameAndText = new ArrayList<>();

        for (int index = 0; index < currentGroup.getInnerGroups().size(); index++) {
            GroupDto group = currentGroup.getInnerGroups().get(index);
            String title = group.getTitle().getOrDefault(
                    sessionDto.getLangCode(),
                    group.getTitle().get("ru")
            );
            if (title != null) {
                nameAndText.add(Pair.of(GROUP_CALLBACK_PREFIX + index, title));
            }
        }

        for (int index = 0; index < currentGroup.getQuestions().size(); index++) {
            QuestionDto question = currentGroup.getQuestions().get(index);
            String title = question.getTitle().getOrDefault(
                    sessionDto.getLangCode(),
                    question.getTitle().get("ru")
            );
            if (title != null) {
                nameAndText.add(Pair.of(QUESTION_CALLBACK_PREFIX + index, title));
            }
        }

        Pair<String, String> back = uiMessageService.getUiMessageNameAndText(UiMessageName.BACK, sessionDto.getLangCode());

        nameAndText.add(back);

        return nameAndText;
    }

    @Nullable
    private GroupDto getSelectedGroup(GroupDto currentGroup, String userInput) {
        Integer index = parseIndex(userInput, GROUP_CALLBACK_PREFIX);
        if (index == null || index < 0 || index >= currentGroup.getInnerGroups().size()) {
            return null;
        }

        return currentGroup.getInnerGroups().get(index);
    }

    @Nullable
    private static QuestionDto getSelectedQuestion(GroupDto currentGroup, String userInput) {
        Integer index = parseIndex(userInput, QUESTION_CALLBACK_PREFIX);
        if (index == null || index < 0 || index >= currentGroup.getQuestions().size()) {
            return null;
        }

        return currentGroup.getQuestions().get(index);
    }

    @Nullable
    private static Integer parseIndex(String userInput, String prefix) {
        if (userInput == null || !userInput.startsWith(prefix)) {
            return null;
        }

        try {
            return Integer.parseInt(userInput.substring(prefix.length()));
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
