package ru.vsu.tgbot.services.sessionstate;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.response.QuestionResponseDto;
import ru.vsu.tgbot.services.business.GroupWindowService;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.BotState;

import java.util.List;

@Service
@AllArgsConstructor
public class QuestionSessionState implements SessionState {
    private final GroupWindowService groupWindowService;

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

        QuestionResponseDto question = sessionDto.getGroupWindow()
                .getLast()
                .questions()
                .getFirst();

        builder.text(question.getText());

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                MessageUtil.getBackButtonRow(sessionDto),
                MessageUtil.getToStartButtonRow(sessionDto)
        ));

        builder.replyMarkup(markup);

        return builder.build();
    }

    private void listen(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.SEND);

        if (MessageUtil.isBackButton(sessionDto)) {
            groupWindowService.moveBackward(sessionDto);
            return;
        }

        if (MessageUtil.isToStartButton(sessionDto)) {
            groupWindowService.moveToStart(sessionDto);
        }
    }

    @Override
    public MessageState getState() {
        return MessageState.QUESTION;
    }
}
