package ru.vsu.tgbot.services.statehandler.global;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;

public interface MainMenuHandler {
    SendMessage create(SessionDto sessionDto);
    boolean listen(SessionDto sessionDto);

}
