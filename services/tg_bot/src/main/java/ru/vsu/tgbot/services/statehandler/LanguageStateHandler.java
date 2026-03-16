package ru.vsu.tgbot.services.statehandler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.model.SessionInfo;
import ru.vsu.tgbot.services.session.SessionService;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.Language;
import ru.vsu.tgbot.util.MessageState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class LanguageStateHandler implements StateHandler{
    private SessionService sessionService;

    @Override
    public SendMessage handle(Long chatId, String text, SessionInfo session) {
        if (session.getMessageState() == MessageState.ANSWER) {
            return answer(chatId, text, session);
        } else {
            return listen(chatId, text, session);
        }
    }


    private SendMessage answer(Long chatId, String text, SessionInfo session) {
        SendMessage.SendMessageBuilder<?, ?> messageBuilder = SendMessage.builder();

        messageBuilder.chatId(chatId);

        messageBuilder.text(text);

        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        for (Language language : Language.values()) {
            InlineKeyboardRow row = new InlineKeyboardRow();
            InlineKeyboardButton button = new InlineKeyboardButton(language.name());

            row.add(button);
            keyboardRows.add(row);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboardRows);

        messageBuilder.replyMarkup(markup);

        session.setMessageState(MessageState.LISTEN);
        sessionService.saveSession(chatId, session);

        return messageBuilder.build();
    }

    private SendMessage listen(Long chatId, String text, SessionInfo session) {
        Language language = Arrays.stream(Language.values())
                .filter(lang -> lang.getValue().equals(text))
                .findFirst()
                .orElse(null);

        if (language == null) {
            return null;
        }

        session.setLanguage(language.getValue());
        sessionService.saveSession(chatId, session);

        return SendMessage
                .builder()
                .chatId(chatId)
                .text("TO DO")
                .build();
    }

    @Override
    public BotState getState() {
        return BotState.LANGUAGE;
    }


}
