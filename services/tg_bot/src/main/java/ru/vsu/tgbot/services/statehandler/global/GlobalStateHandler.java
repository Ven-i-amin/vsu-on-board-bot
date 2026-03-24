package ru.vsu.tgbot.services.statehandler.global;

import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;

public interface GlobalStateHandler {
    boolean handle(SessionDto sessionDto, BotMessageSender sender);
}
