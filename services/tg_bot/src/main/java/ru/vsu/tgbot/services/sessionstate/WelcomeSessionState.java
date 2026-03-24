package ru.vsu.tgbot.services.sessionstate;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.services.core.GroupService;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.UiMessage;

@Service
@AllArgsConstructor
public class WelcomeSessionState implements SessionState {
    private final GroupService groupService;
    private final UiMessageControl uiMessageService;

    @Override
    public void handle(SessionDto sessionDto, BotMessageSender sender) {
        sessionDto.setBotState(BotState.SEND);

        GroupDto startGroup = groupService.getStartGroup();

        sessionDto.setStart(startGroup);
        sessionDto.getGroupWindow().clear();

        if (sessionDto.getLangCode() == null) {
            sessionDto.setMessageState(MessageState.LANGUAGE);
            return;
        }

        sessionDto.setMessageState(MessageState.MAIN_MENU);

        sender.send(SendMessage
                .builder()
                .chatId(sessionDto.getChatId())
                .text(uiMessageService.getUiMessageText(UiMessage.WELCOME, sessionDto.getLangCode()))
                .build());
    }

    @Override
    public MessageState getState() {
        return MessageState.WELCOME;
    }
}
