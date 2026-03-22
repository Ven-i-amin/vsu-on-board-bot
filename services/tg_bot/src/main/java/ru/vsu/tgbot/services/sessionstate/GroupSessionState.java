package ru.vsu.tgbot.services.sessionstate;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.QuestionDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.GroupWindowService;
import ru.vsu.tgbot.services.business.UiMessageControlService;
import ru.vsu.tgbot.util.StateHandlerUtil;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.UiMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@AllArgsConstructor
public class GroupSessionState implements SessionState {
    public static final int GROUP_ROW_SIZE = 1;
    private final GroupWindowService groupWindowService;
    private final UiMessageControlService uiMessageService;

    @Override
    public void handle(SessionDto sessionDto, BotMessageSender sender) {
        if (sessionDto.getBotState() == BotState.SEND) {
            sender.send(answer(sessionDto));
        } else {
            listen(sessionDto);
        }
    }

    @Override
    public MessageState getState() {
        return MessageState.GROUP;
    }

    private SendMessage answer(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.LISTEN);
        SendMessage.SendMessageBuilder<?, ?> builder = SendMessage.builder();

        builder.chatId(sessionDto.getChatId());

        List<InlineKeyboardRow> column = StateHandlerUtil.getButtonColumn(
                getAllTitles(sessionDto),
                GROUP_ROW_SIZE
        );

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(column);

        return builder.replyMarkup(inlineKeyboardMarkup).build();
    }

    private void listen(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.SEND);

        String text = sessionDto.getText();

        GroupDto currentGroup = StateHandlerUtil.getCurrentGroup(sessionDto.getGroupWindow());

        GroupDto selectedGroup = currentGroup.innerGroups().stream()
                .filter(gr -> gr.title().get(sessionDto.getLangCode()).equals(text))
                .findFirst()
                .orElse(null);

        if (selectedGroup != null) {
            groupWindowService.moveForward(sessionDto, selectedGroup);
            return;
        }

        QuestionDto selectedQuestion = currentGroup.questions().stream()
                .filter(question -> question.getTitle().get(sessionDto.getLangCode()).equals(text))
                .findFirst()
                .orElse(null);

        if (selectedQuestion != null) {
            GroupDto questionGroup = GroupDto.builder()
                    .title(Map.of())
                    .questions(List.of(selectedQuestion))
                    .parentId(selectedQuestion.getParent().parentId())
                    .build();

            groupWindowService.moveForward(sessionDto, questionGroup);
            sessionDto.setMessageState(MessageState.QUESTION);
            return;
        }

        if (text.equals(UiMessage.BACK.getValue())) {
            groupWindowService.moveBackward(sessionDto);
            return;
        }

        sessionDto.setBotState(BotState.LISTEN);
    }

    private List<String> getAllTitles(SessionDto sessionDto) {
        GroupDto currentGroup = sessionDto.getGroupWindow().getLast();
        List<String> titles = new ArrayList<>();

        titles.addAll(
                currentGroup.innerGroups().stream()
                        .map(GroupDto::title)
                        .map(el -> el.get(sessionDto.getLangCode()))
                        .filter(Objects::nonNull)
                        .toList()
        );

        titles.addAll(
                currentGroup.questions().stream()
                        .map(QuestionDto::getTitle)
                        .map(el -> el.get(sessionDto.getLangCode()))
                        .filter(Objects::nonNull)
                        .toList()
        );

        String backText = uiMessageService.getUiMessageText(UiMessage.BACK, sessionDto.getLangCode());

        titles.add(backText);

        return titles;
    }
}
