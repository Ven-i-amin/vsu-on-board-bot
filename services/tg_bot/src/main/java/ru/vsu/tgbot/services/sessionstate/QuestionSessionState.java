package ru.vsu.tgbot.services.sessionstate;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.QuestionDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.GroupWindowService;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.UiMessage;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionSessionState implements SessionState {
    private final GroupWindowService groupWindowService;
    private final UiMessageControl uiMessageService;

    @Override
    public void handle(SessionDto sessionDto, BotMessageSender sender) {
        if (sessionDto.getBotState() == BotState.SEND) {
            sender.send(answer(sessionDto));
        } else {
            listen(sessionDto);
        }
    }

    private SendMessage answer(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.LISTEN);
        SendMessage.SendMessageBuilder<?, ?> builder = SendMessage.builder();

        builder.chatId(sessionDto.getChatId());

        QuestionDto question = sessionDto.getGroupWindow()
                .getLast()
                .questions()
                .getFirst();

        builder.text(question.getText().get(sessionDto.getLangCode()));

        List<InlineKeyboardRow> column = MessageUtil.getInlineButtonColumn(
                List.of(
                uiMessageService.getUiMessageText(UiMessage.BACK, sessionDto.getLangCode()),
                uiMessageService.getUiMessageText(UiMessage.START, sessionDto.getLangCode())
                ),
                1
        );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(column);

        builder.replyMarkup(markup);

        return builder.build();
    }

    private void listen(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.SEND);

        String text = sessionDto.getText();

        if (text.equals(UiMessage.BACK.getValue())) {
            groupWindowService.moveBackward(sessionDto);
            return;
        }

        if (text.equals(UiMessage.START.getValue())) {
            groupWindowService.moveToStart(sessionDto);
        }
    }

    @Override
    public MessageState getState() {
        return MessageState.QUESTION;
    }
}
