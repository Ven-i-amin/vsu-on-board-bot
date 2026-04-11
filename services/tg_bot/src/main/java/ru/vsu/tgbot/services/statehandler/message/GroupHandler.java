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
import ru.vsu.tgbot.services.business.GroupService;
import ru.vsu.tgbot.services.business.UiMessageService;
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
    private final GroupService groupService;
    private final UiMessageService uiMessageService;

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

        String translatedTitle = groupTitle.getOrDefault(sessionDto.getLangCode(), NOT_FOUND_MESSAGE);

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
        GroupDto selectedGroup = getSelectedGroup(currentGroup,  userInput);

        if (selectedGroup != null) {
            groupService.moveForward(sessionDto, selectedGroup);
            return true;
        }

        QuestionDto selectedQuestion = getSelectedQuestion(currentGroup, userInput);

        if (selectedQuestion != null) {
            GroupDto questionGroup = createGroupForQuestion(selectedQuestion);

            groupService.moveForward(sessionDto, questionGroup);
            sessionDto.setMessageState(MessageState.QUESTION);
            return true;
        }

        if (userInput.equals(UiMessageName.BACK.getValue())) {
            groupService.moveBackward(sessionDto);
            return true;
        }

        return false;
    }

    private List<Pair<String, String>> getAllTitles(SessionDto sessionDto) {
        GroupDto currentGroup = sessionDto.getGroupWindow().getLast();
        List<Pair<String, String>> nameAndText = new ArrayList<>();

        nameAndText.addAll(MessageUtil.getLocalizedGroupNameAndTitles(currentGroup, sessionDto));
        nameAndText.addAll(MessageUtil.getLocalizedQuestionNameAndTitles(currentGroup, sessionDto));

        Pair<String, String> back = uiMessageService.getUiMessageNameAndText(UiMessageName.BACK, sessionDto.getLangCode());

        nameAndText.add(back);

        return nameAndText;
    }

    @Nullable
    private GroupDto getSelectedGroup(GroupDto currentGroup, String userInput) {
        return currentGroup.getInnerGroups().stream()
                .filter(gr -> gr.getName().equals(userInput))
                .findFirst()
                .orElse(null);
    }

    @Nullable
    private static QuestionDto getSelectedQuestion(GroupDto currentGroup, String userInput) {
        return currentGroup.getQuestions().stream()
                .filter(question -> question.getName().equals(userInput))
                .findFirst()
                .orElse(null);
    }
}
