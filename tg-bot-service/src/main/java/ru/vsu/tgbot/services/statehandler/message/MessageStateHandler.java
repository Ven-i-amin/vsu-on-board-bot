package ru.vsu.tgbot.services.statehandler.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.util.MessageState;

public interface MessageStateHandler {

    SendMessage answer(SessionDto sessionDto);
    boolean listen(SessionDto sessionDto);

    MessageState getState();
}
