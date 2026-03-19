package ru.vsu.tgbot.services.sessionstate;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.LanguageDto;
import ru.vsu.tgbot.model.dto.QuestionDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.core.LanguageService;
import ru.vsu.tgbot.util.GroupUtil;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.BotState;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class LanguageSessionState implements SessionState {
    private final LanguageService languageService;

    private List<LanguageDto> languages;

    @PostConstruct
    private void init() {
        languages = languageService.getLanguages();
    }

    @Override
    public void handle(SessionDto sessionDto, BotMessageSender sender) {
        if (sessionDto.getBotState() == BotState.SEND) {
            sender.send(answer(sessionDto));
        } else {
            SendMessage message = listen(sessionDto);

            if (message != null) {
                sender.send(message);
            }
        }
    }


    private SendMessage answer(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.LISTEN);

        SendMessage.SendMessageBuilder<?, ?> messageBuilder = SendMessage.builder();
        messageBuilder.chatId(sessionDto.getChatId());

        QuestionDto question = GroupUtil.getSpecialQuestion(sessionDto, "question_listen");
        messageBuilder.text(question.getText());

        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        for (LanguageDto language : languages) {
            InlineKeyboardRow row = new InlineKeyboardRow();
            InlineKeyboardButton button = new InlineKeyboardButton(language.name());

            row.add(button);
            keyboardRows.add(row);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboardRows);

        messageBuilder.replyMarkup(markup);

        return messageBuilder.build();
    }

    private SendMessage listen(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.SEND);

        LanguageDto language = languages.stream()
                .filter(lang -> lang.name().equals(sessionDto.getText()))
                .findFirst()
                .orElse(null);

        if (language == null) {
            return null;
        }

        sessionDto.setLanguage(language.code());

        QuestionDto question = GroupUtil.getSpecialQuestion(sessionDto, "question_answer");

        return SendMessage
                .builder()
                .chatId(sessionDto.getChatId())
                .text(question.getText())
                .build();
    }

    @Override
    public MessageState getState() {
        return MessageState.LANGUAGE;
    }
}
