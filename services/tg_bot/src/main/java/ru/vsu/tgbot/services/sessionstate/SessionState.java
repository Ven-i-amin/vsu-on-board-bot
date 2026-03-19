package ru.vsu.tgbot.services.sessionstate;

import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.util.MessageState;

public interface SessionState {

    void handle(SessionDto sessionDto, BotMessageSender sender);
    MessageState getState();
}
