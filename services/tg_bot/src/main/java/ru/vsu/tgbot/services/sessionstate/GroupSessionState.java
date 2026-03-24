package ru.vsu.tgbot.services.sessionstate;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.QuestionDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.GroupWindowService;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.UiMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class GroupSessionState implements SessionState {
    public static final int GROUP_ROW_SIZE = 1;
    private final GroupWindowService groupWindowService;
    private final UiMessageControl uiMessageService;

    @Override
    public void handle(SessionDto sessionDto, BotMessageSender sender) {
        if (sessionDto.getBotState() == BotState.SEND) {
            SendMessage answer = answer(sessionDto);

            if (answer != null) {
                sender.send(answer(sessionDto));
            }
        } else {
            listen(sessionDto);
        }
    }

    @Override
    public MessageState getState() {
        return MessageState.GROUP;
    }

    private SendMessage answer(SessionDto sessionDto) {
        if (sessionDto.getGroupWindow().isEmpty()) {
            sessionDto.setMessageState(MessageState.MAIN_MENU);

            return null;
        }

        sessionDto.setBotState(BotState.LISTEN);
        SendMessage.SendMessageBuilder<?, ?> builder = SendMessage.builder();

        builder.chatId(sessionDto.getChatId());
        builder.text(sessionDto.getGroupWindow().getLast().getTitle().get(sessionDto.getLangCode()));

        List<InlineKeyboardRow> column = MessageUtil.getInlineButtonColumn(
                getAllTitles(sessionDto),
                GROUP_ROW_SIZE
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(column);

        return builder.replyMarkup(inlineKeyboardMarkup).build();
    }

    private void listen(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.SEND);

        String text = MessageUtil.extractUserInput(sessionDto.getUpdate());
        if (text == null) {
            sessionDto.setBotState(BotState.LISTEN);
            return;
        }

        if (sessionDto.getGroupWindow().isEmpty()) {
            sessionDto.setMessageState(MessageState.MAIN_MENU);
            return;
        }

        GroupDto currentGroup = MessageUtil.getCurrentGroup(sessionDto.getGroupWindow());

        GroupDto selectedGroup = currentGroup.getInnerGroups().stream()
                .filter(gr -> gr.getName().equals(text))
                .findFirst()
                .orElse(null);

        if (selectedGroup != null) {
            groupWindowService.moveForward(sessionDto, selectedGroup);
            return;
        }

        QuestionDto selectedQuestion = currentGroup.getQuestions().stream()
                .filter(question -> question.getName().equals(text))
                .findFirst()
                .orElse(null);

        if (selectedQuestion != null) {
            GroupDto questionGroup = GroupDto.builder()
                    .title(Map.of())
                    .questions(List.of(selectedQuestion))
                    .innerGroups(new ArrayList<>())
                    .parentName(currentGroup.getParentName())
                    .build();

            groupWindowService.moveForward(sessionDto, questionGroup);
            sessionDto.setMessageState(MessageState.QUESTION);
            return;
        }

        if (text.equals(UiMessage.BACK.getValue())) {
            groupWindowService.moveBackward(sessionDto);

            if (sessionDto.getGroupWindow().isEmpty()) {
                sessionDto.setMessageState(MessageState.MAIN_MENU);
                return;
            }

            return;
        }

        sessionDto.setBotState(BotState.LISTEN);
    }

    private List<Pair<String, String>> getAllTitles(SessionDto sessionDto) {
        GroupDto currentGroup = sessionDto.getGroupWindow().getLast();
        List<Pair<String, String>> nameAndText = new ArrayList<>();

        nameAndText.addAll(MessageUtil.getLocalizedGroupNameAndTitles(currentGroup, sessionDto));
        nameAndText.addAll(MessageUtil.getLocalizedQuestionNameAndTitles(currentGroup, sessionDto));

        Pair<String, String> back = uiMessageService.getUiMessageNameAndText(UiMessage.BACK, sessionDto.getLangCode());

        nameAndText.add(back);

        return nameAndText;
    }
}
