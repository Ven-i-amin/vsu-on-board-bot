package ru.vsu.tgbot.services.statehandler.bot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.components.registry.MessageHandlerRegistry;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.statehandler.global.MainMenuHandler;
import ru.vsu.tgbot.services.statehandler.message.MessageStateHandler;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MainMenuState;

@Service
@AllArgsConstructor
public class ListenHandler implements BotStateHandler {
    private MainMenuHandler globalHandler;
    private MessageHandlerRegistry messageRegistry;

    @Override
    public void handle(SessionDto sessionDto, BotMessageSender sender) {
        if (handledByMainMenu(sessionDto)
                || handledByMessageHandler(sessionDto)) {
            sessionDto.setBotState(BotState.DELETE);
        } else {
            sessionDto.setBotState(BotState.SEND);
        }
    }

    @Override
    public BotState getState() {
        return BotState.LISTEN;
    }

    private boolean handledByMainMenu(SessionDto sessionDto) {
        return sessionDto.getGlobalState() == MainMenuState.LISTEN
                && globalHandler.listen(sessionDto);
    }

    private boolean handledByMessageHandler(SessionDto sessionDto) {
        MessageStateHandler messageHandler = messageRegistry.getHandler(sessionDto.getMessageState());

        if (messageHandler == null) {
            return false;
        }

        return messageHandler.listen(sessionDto);
    }
}
