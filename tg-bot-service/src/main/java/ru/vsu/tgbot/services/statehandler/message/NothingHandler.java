package ru.vsu.tgbot.services.statehandler.message;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MessageState;

@Service
@AllArgsConstructor
public class NothingHandler implements MessageStateHandler{

    @Override
    public SendMessage answer(SessionDto sessionDto) {
        return null;
    }

    @Override
    public boolean listen(SessionDto sessionDto) {
        return true;
    }

    @Override
    public MessageState getState() {
        return MessageState.NOTHING;
    }
}
