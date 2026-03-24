package ru.vsu.tgbot.services.statehandler.message;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MessageState;

@Service
@AllArgsConstructor
public class NothingHandler implements MessageStateHandler{
    @Override
    public void handle(SessionDto sessionDto, BotMessageSender sender) {
        sessionDto.setBotState(BotState.LISTEN);
    }

    @Override
    public MessageState getState() {
        return MessageState.NOTHING;
    }
}
