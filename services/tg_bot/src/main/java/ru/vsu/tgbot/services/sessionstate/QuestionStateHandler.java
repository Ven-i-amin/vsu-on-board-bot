package ru.vsu.tgbot.services.statehandler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.model.QuestionAndAnswer;
import ru.vsu.tgbot.model.SessionInfo;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.sessionstate.SessionState;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MessageState;

import java.util.ArrayList;
import java.util.List;

public class QuestionStateHandler implements SessionState {
    @Override
    public SendMessage handle(SessionDto sessionDto) {
        if (sessionDto.messageState() == MessageState.ANSWER) {
            return answer(sessionDto);
        }
    }

    @Override
    public BotState getState() {
        return null;
    }

    private SendMessage answer(SessionDto sessionDto) {
        SendMessage.SendMessageBuilder<?, ?> builder = SendMessage.builder();

        builder.chatId(chatId);

        List<InlineKeyboardRow> rows = new ArrayList<>();

        for (QuestionAndAnswer questionAndAnswer : sessionDto.group()) {
            InlineKeyboardButton button = new InlineKeyboardButton(questionAndAnswer);
            InlineKeyboardRow row = new InlineKeyboardRow();

            row.add(button);
            rows.add(row);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);

        return builder.replyMarkup(inlineKeyboardMarkup).build();
    }
}
