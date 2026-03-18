package ru.vsu.tgbot.services.sessionstate;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.util.BotState;

public interface SessionState {
    SendMessage handle(SessionDto sessionDto);
    BotState getState();

}
