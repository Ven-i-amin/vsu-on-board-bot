package ru.vsu.tgbot.services.statehandler.global;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.GroupWindowService;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.util.*;

import java.util.List;

@Service
@AllArgsConstructor
public class MainMenuHandler implements GlobalStateHandler {
    public static final int MAIN_LANGUAGE_ROW_SIZE = 1;
    public static final int MAIN_GROUP_ROW_SIZE = 2;
    private UiMessageControl uiMessageControl;
    private GroupWindowService groupWindowService;

    @Override
    public boolean handle(SessionDto sessionDto, BotMessageSender sender) {
        if (sessionDto.getGlobalState() == GlobalState.CREATE) {
            sender.send(create(sessionDto));
            return true;
        } else if (sessionDto.getGlobalState() == GlobalState.LISTEN) {
            return listen(sessionDto);
        }

        return false;
    }

    private boolean listen(SessionDto sessionDto) {
        String text = MessageUtil.extractUserInput(sessionDto.getUpdate());

        if (text == null) {
            return true;
        }

        if (isLanguageState(text, sessionDto)) {
            sessionDto.setBotState(BotState.SEND);
            sessionDto.setMessageState(MessageState.LANGUAGE);

            return true;
        }

        GroupDto selectedGroup = MessageUtil.getGroupByText(text, sessionDto);

        if (selectedGroup != null) {
            sessionDto.setBotState(BotState.SEND);
            sessionDto.setMessageState(MessageState.GROUP);

            groupWindowService.moveToStart(sessionDto);
            groupWindowService.moveForward(sessionDto, selectedGroup);
            return true;
        }

        return false;
    }

    private SendMessage create(SessionDto sessionDto) {
        sessionDto.setGlobalState(GlobalState.LISTEN);

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
                .text(uiMessageControl.getUiMessageText(
                        UiMessage.MAIN_MENU,
                        sessionDto.getLangCode())
                )
                .replyMarkup(replyKeyboardMarkup)
                .build();
    }

    private boolean isLanguageState(String text, SessionDto sessionDto) {
        return text.equals(uiMessageControl.getUiMessageText(UiMessage.LANGUAGE_TITLE, sessionDto.getLangCode()));
    }
}
