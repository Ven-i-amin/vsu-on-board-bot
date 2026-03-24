package ru.vsu.tgbot.services.statehandler.bot;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.util.BotState;

@Service
public class DeleteHandler implements BotStateHandler {

    @Override
    public void handle(SessionDto sessionDto, BotMessageSender sender) {
        if (sessionDto.getBotState() != BotState.DELETE) {
            return;
        }

        if (sessionDto.getLastMessageId() == null) {
            sessionDto.setBotState(BotState.SEND);
            return;
        }

        sender.delete(
                DeleteMessage.builder()
                        .chatId(sessionDto.getChatId())
                        .messageId(sessionDto.getLastMessageId())
                        .build()
        );

        sessionDto.setBotState(BotState.SEND);
    }
}
