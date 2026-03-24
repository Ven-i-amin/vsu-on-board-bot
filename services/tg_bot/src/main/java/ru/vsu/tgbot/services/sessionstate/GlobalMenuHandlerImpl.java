package ru.vsu.tgbot.services.sessionstate;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.vsu.tgbot.components.bot.BotMessageSender;
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
public class GlobalMenuHandlerImpl implements SessionState {
    public static final int MAIN_LANGUAGE_ROW_SIZE = 1;
    public static final int MAIN_GROUP_ROW_SIZE = 2;
    private UiMessageControl uiMessageControl;
    private GroupWindowService groupWindowService;

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
        return MessageState.MAIN_MENU;
    }

    private SendMessage answer(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.LISTEN);

        List<KeyboardRow> keyboardRows = MessageUtil.getButtonColumn(
                List.of(uiMessageControl.getUiMessageText(
                        UiMessage.LANGUAGE_TITLE,
                        sessionDto.getLangCode())
                ),
                MAIN_LANGUAGE_ROW_SIZE
        );

        keyboardRows.addAll(
                MessageUtil.getButtonColumn(
                        MessageUtil.getLocalizedGroupTitles(sessionDto),
                        MAIN_GROUP_ROW_SIZE
                )
        );

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);

        return SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(uiMessageControl.getUiMessageText(UiMessage.MAIN_MENU, sessionDto.getLangCode()))
                .replyMarkup(replyKeyboardMarkup)
                .build();
    }

    private void listen(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.SEND);

        String text = MessageUtil.extractUserInput(sessionDto.getUpdate());
        if (text == null) {
            sessionDto.setBotState(BotState.LISTEN);
            return;
        }

        if (text.equals(uiMessageControl.getUiMessageText(UiMessage.LANGUAGE_TITLE, sessionDto.getLangCode()))) {
            sessionDto.setMessageState(MessageState.LANGUAGE);
            return;
        }

        var selectedGroup = MessageUtil.getGroupByText(text, sessionDto);
        if (selectedGroup == null) {
            sessionDto.setBotState(BotState.LISTEN);
            return;
        }

        sessionDto.setMessageState(MessageState.GROUP);
        groupWindowService.moveToStart(sessionDto);
        groupWindowService.moveForward(sessionDto, selectedGroup);
    }
}
