package ru.vsu.tgbot.services.statehandler.global;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.GroupWindowService;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.util.*;

import java.util.List;

@Service
@AllArgsConstructor
public class MainMenuHandlerImpl implements MainMenuHandler {
    public static final int MAIN_LANGUAGE_ROW_SIZE = 1;
    public static final int MAIN_GROUP_ROW_SIZE = 2;
    private UiMessageControl uiMessageControl;
    private GroupWindowService groupWindowService;

    @Override
    public SendMessage create(SessionDto sessionDto) {
        sessionDto.setGlobalState(MainMenuState.LISTEN);

        List<KeyboardRow> keyboardRows = MessageUtil.createButtonColumn(
                List.of(uiMessageControl.getUiMessageText(
                        UiMessageName.LANGUAGE_TITLE,
                        sessionDto.getLangCode())
                ),
                MAIN_LANGUAGE_ROW_SIZE
        );

        keyboardRows.addAll(
                MessageUtil.createButtonColumn(
                        MessageUtil.getLocalizedGroupTitles(sessionDto),
                        MAIN_GROUP_ROW_SIZE
                )
        );

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);

        return SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(uiMessageControl.getUiMessageText(
                        UiMessageName.MAIN_MENU,
                        sessionDto.getLangCode())
                )
                .replyMarkup(replyKeyboardMarkup)
                .build();
    }

    @Override
    public boolean listen(SessionDto sessionDto) {
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

    private boolean isLanguageState(String text, SessionDto sessionDto) {
        return text.equals(uiMessageControl.getUiMessageText(UiMessageName.LANGUAGE_TITLE, sessionDto.getLangCode()));
    }
}
