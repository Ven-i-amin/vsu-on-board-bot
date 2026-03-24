package ru.vsu.tgbot.services.statehandler.message;

import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.util.MessageState;

public interface MessageStateHandler {

    void handle(SessionDto sessionDto, BotMessageSender sender);
    MessageState getState();
}
