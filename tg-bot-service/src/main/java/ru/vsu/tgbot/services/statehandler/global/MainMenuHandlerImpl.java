package ru.vsu.tgbot.services.statehandler.global;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.GroupNavigationService;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.services.core.GroupService;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MainMenuState;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.UiMessageName;

import java.util.List;

@Service
@AllArgsConstructor
public class MainMenuHandlerImpl implements MainMenuHandler {
    public static final int MAIN_LANGUAGE_ROW_SIZE = 1;
    public static final int MAIN_GROUP_ROW_SIZE = 2;

    private final UiMessageControl uiMessageControl;
    private final GroupNavigationService groupNavigationService;
    private final GroupService groupService;

    @Override
    public SendMessage create(SessionDto sessionDto) {
        sessionDto.setGlobalState(MainMenuState.LISTEN);
        groupNavigationService.goToRoot(sessionDto.getChatId());

        GroupDto rootGroup = groupService.getRootGroupWithContent();

        List<KeyboardRow> keyboardRows = MessageUtil.createButtonColumn(
                List.of(uiMessageControl.getUiMessageText(UiMessageName.LANGUAGE_TITLE, sessionDto.getLangCode())),
                MAIN_LANGUAGE_ROW_SIZE
        );

        List<String> groupTitles = MessageUtil.getLocalizedGroupTitles(rootGroup, sessionDto.getLangCode());
        keyboardRows.addAll(MessageUtil.createButtonColumn(groupTitles, MAIN_GROUP_ROW_SIZE));

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);

        return SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(uiMessageControl.getUiMessageText(UiMessageName.MAIN_MENU, sessionDto.getLangCode()))
                .replyMarkup(replyKeyboardMarkup)
                .build();
    }

    @Override
    public boolean listen(SessionDto sessionDto) {
        String text = MessageUtil.extractUserInput(sessionDto.getUpdate());
        if (text == null) return true;

        if (isLanguageState(text, sessionDto)) {
            sessionDto.setBotState(BotState.SEND);
            sessionDto.setMessageState(MessageState.LANGUAGE);
            return true;
        }

        GroupDto rootGroup = groupService.getRootGroupWithContent();
        GroupDto selectedGroup = MessageUtil.getGroupByText(text, rootGroup, sessionDto.getLangCode());

        if (selectedGroup != null) {
            sessionDto.setBotState(BotState.SEND);
            sessionDto.setMessageState(MessageState.GROUP);
            groupNavigationService.setCurrentGroup(sessionDto.getChatId(), selectedGroup.getName());
            return true;
        }

        return false;
    }

    private boolean isLanguageState(String text, SessionDto sessionDto) {
        return text.equals(uiMessageControl.getUiMessageText(UiMessageName.LANGUAGE_TITLE, sessionDto.getLangCode()));
    }
}
