package ru.vsu.tgbot.services.statehandler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.vsu.tgbot.model.SessionInfo;
import ru.vsu.tgbot.util.BotState;

public interface StateHandler {
    SendMessage handle(Long chatId, String text, SessionInfo session);
    BotState getState();

}
