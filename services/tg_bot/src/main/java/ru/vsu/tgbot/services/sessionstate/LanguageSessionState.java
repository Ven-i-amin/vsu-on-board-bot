package ru.vsu.tgbot.services.sessionstate;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.session.SessionService;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.Language;
import ru.vsu.tgbot.util.MessageState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class LanguageSessionState implements SessionState {
    private SessionService sessionService;

    @Override
    public SendMessage handle(SessionDto sessionDto) {
        if (sessionDto.messageState() == MessageState.ANSWER) {
            return answer(sessionDto);
        } else {
            return listen(sessionDto);
        }
    }


    private SendMessage answer(SessionDto sessionInfo) {
        SendMessage.SendMessageBuilder<?, ?> messageBuilder = SendMessage.builder();

        messageBuilder.chatId(sessionInfo.chatId());

        messageBuilder.text(sessionInfo.text());

        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        for (Language language : Language.values()) {
            InlineKeyboardRow row = new InlineKeyboardRow();
            InlineKeyboardButton button = new InlineKeyboardButton(language.name());

            row.add(button);
            keyboardRows.add(row);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboardRows);

        messageBuilder.replyMarkup(markup);


        sessionService.saveSession(chatId, session);

        return messageBuilder.build();
    }

    private SendMessage listen(SessionDto sessionInfo) {
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
