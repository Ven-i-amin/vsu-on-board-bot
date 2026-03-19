package ru.vsu.tgbot.services.sessionstate;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.response.GroupResponseDto;
import ru.vsu.tgbot.model.response.QuestionResponseDto;
import ru.vsu.tgbot.services.business.GroupWindowService;
import ru.vsu.tgbot.util.GroupUtil;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupSessionState implements SessionState {
    private final GroupWindowService groupWindowService;

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

        List<InlineKeyboardRow> rows = getInlineKeyboardRows(getAllTitles(sessionDto));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);

        return builder.replyMarkup(inlineKeyboardMarkup).build();
    }

    private void listen(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.SEND);

        String text = sessionDto.getText();

        GroupResponseDto currentGroup = GroupUtil.getCurrentGroup(sessionDto.getGroupWindow());

        GroupResponseDto selectedGroup = currentGroup.innerGroups().stream()
                .filter(gr -> gr.title().equals(text))
                .findFirst()
                .orElse(null);

        if (selectedGroup != null) {
            groupWindowService.moveForward(sessionDto, selectedGroup);
            return;
        }

        QuestionResponseDto selectedQuestion = currentGroup.questions().stream()
                .filter(question -> question.getTitle().equals(text))
                .findFirst()
                .orElse(null);

        if (selectedQuestion != null) {
            GroupResponseDto questionGroup = GroupResponseDto.builder()
                    .title("")
                    .questions(List.of(selectedQuestion))
                    .parentId(selectedQuestion.getParent().parentId())
                    .build();

            groupWindowService.moveForward(sessionDto, questionGroup);
            sessionDto.setMessageState(MessageState.QUESTION);
            return;
        }

        if (MessageUtil.isBackButton(sessionDto)) {
            groupWindowService.moveBackward(sessionDto);
            return;
        }

        sessionDto.setBotState(BotState.LISTEN);
    }

    @NotNull
    private static List<InlineKeyboardRow> getInlineKeyboardRows(List<String> buttonTitles) {
        List<InlineKeyboardRow> rows = new ArrayList<>();

        for (String title : buttonTitles) {
            InlineKeyboardButton button = new InlineKeyboardButton(title);
            InlineKeyboardRow row = new InlineKeyboardRow();

            row.add(button);
            rows.add(row);
        }

        return rows;
    }

    private static List<String> getAllTitles(SessionDto sessionDto) {
        GroupResponseDto currentGroup = sessionDto.getGroupWindow().getLast();
        List<String> titles = new ArrayList<>();

        titles.addAll(currentGroup.innerGroups().stream().map(GroupResponseDto::title).toList());
        titles.addAll(currentGroup.questions().stream().map(QuestionResponseDto::getTitle).toList());

        QuestionResponseDto back = GroupUtil.getSpecialQuestion(sessionDto, "back");

        titles.add(back.getTitle());

        return titles;
    }
}
