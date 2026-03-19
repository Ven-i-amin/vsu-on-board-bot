package ru.vsu.tgbot.services.sessionstate;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.QuestionDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.core.GroupService;
import ru.vsu.tgbot.util.GroupUtil;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.BotState;

@Service
@AllArgsConstructor
public class WelcomeSessionState implements SessionState {
    private GroupService groupService;

    @Override
    public void handle(SessionDto sessionDto, BotMessageSender sender) {
        sessionDto.setBotState(BotState.SEND);

        GroupDto startGroup;

        if (sessionDto.getLanguage() == null) {
            sessionDto.setMessageState(MessageState.LANGUAGE);

            startGroup = groupService.getStartGroup();

            sessionDto.setStart(startGroup);
            sessionDto.getGroupWindow().add(startGroup);

            return;
        }

        startGroup = groupService.getStartGroup(sessionDto.getLanguage());

        sessionDto.setStart(startGroup);
        sessionDto.getGroupWindow().add(startGroup);

        QuestionDto welcome = GroupUtil.getSpecialQuestion(sessionDto, "welcome");

        sender.send(SendMessage
                .builder()
                .chatId(sessionDto.getChatId())
                .text(welcome.getText())
                .build());
    }

    @Override
    public MessageState getState() {
        return MessageState.WELCOME;
    }
}
