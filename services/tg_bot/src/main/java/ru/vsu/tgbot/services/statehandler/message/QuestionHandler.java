package ru.vsu.tgbot.services.statehandler.message;

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
public class QuestionHandler implements MessageStateHandler {
    private final GroupWindowService groupWindowService;
    private final UiMessageControl uiMessageService;

    @Override
    public void handle(SessionDto sessionDto, BotMessageSender sender) {
        if (sessionDto.getBotState() == BotState.SEND) {
            sessionDto.setLastMessageId(sender.send(answer(sessionDto)).getMessageId());
        } else {
            listen(sessionDto);
        }
    }

    private SendMessage answer(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.LISTEN);

        SendMessage.SendMessageBuilder<?, ?> builder = SendMessage.builder();

        builder.chatId(sessionDto.getChatId());

        String callback = MessageUtil.extractUserInput(sessionDto.getUpdate());
        List<QuestionDto> questions;

        if (sessionDto.getGroupWindow().isEmpty()) {
            questions = sessionDto.getStart().getQuestions();
        } else {
            questions = sessionDto.getGroupWindow().getLast().getQuestions();
        }

        QuestionDto question = questions.stream()
                .filter(el -> el.getName().equals(callback))
                .findFirst()
                .orElse(null);

        String text;

        if (question.getText() == null) {
            text = MessageUtil.NOT_FOUND_MESSAGE;
        } else {
            text = question.getText().getOrDefault(sessionDto.getLangCode(), MessageUtil.NOT_FOUND_MESSAGE);
        }

        builder.text(text);

        List<InlineKeyboardRow> column = MessageUtil.getInlineButtonColumn(
                List.of(
                        uiMessageService.getUiMessageNameAndText(UiMessage.BACK, sessionDto.getLangCode())
//                        uiMessageService.getUiMessageNameAndText(UiMessage.START, sessionDto.getLangCode())
                ),
                2
        );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(column);
        builder.replyMarkup(markup);

        return builder.build();
    }

    private void listen(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.DELETE);

        String text = MessageUtil.extractUserInput(sessionDto.getUpdate());
        if (text == null) {
            sessionDto.setBotState(BotState.LISTEN);
            return;
        }

        if (text.equals(UiMessage.BACK.getValue())) {
            sessionDto.setMessageState(MessageState.GROUP);
            groupWindowService.moveBackward(sessionDto);
            return;
        }

        if (text.equals(UiMessage.START.getValue())) {
            sessionDto.setMessageState(MessageState.NOTHING);
            groupWindowService.moveToStart(sessionDto);
            return;
        }

        sessionDto.setBotState(BotState.LISTEN);
    }

    @Override
    public MessageState getState() {
        return MessageState.QUESTION;
    }
}
