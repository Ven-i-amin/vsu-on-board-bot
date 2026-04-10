package ru.vsu.tgbot.services.statehandler.message;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.services.core.GroupService;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MainMenuState;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.UiMessageName;

@Service
@AllArgsConstructor
public class WelcomeHandler implements MessageStateHandler {
    private final GroupService groupService;
    private final UiMessageControl uiMessageService;


    @Override
    public SendMessage answer(SessionDto sessionDto) {
        GroupDto startGroup = groupService.getStartGroup();

        sessionDto.setStart(startGroup);
        sessionDto.getGroupWindow().clear();

        if (sessionDto.getLangCode() == null) {
            sessionDto.setMessageState(MessageState.LANGUAGE);
            return null;
        }

        sessionDto.setGlobalState(MainMenuState.CREATE);
        sessionDto.setMessageState(MessageState.NOTHING);
        sessionDto.setBotState(BotState.SEND);

        return SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(uiMessageService.getUiMessageText(
                        UiMessageName.WELCOME,
                        sessionDto.getLangCode()
                ))
                .build();
    }

    @Override
    public boolean listen(SessionDto sessionDto) {
        return false;
    }

    @Override
    public MessageState getState() {
        return MessageState.WELCOME;
    }
}
