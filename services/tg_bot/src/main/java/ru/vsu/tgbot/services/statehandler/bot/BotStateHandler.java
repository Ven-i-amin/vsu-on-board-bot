package ru.vsu.tgbot.services.statehandler.bot;

import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;

public interface BotStateHandler {
    void handle(SessionDto sessionDto, BotMessageSender sender);
}
