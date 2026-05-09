package ru.vsu.tgbot.components.registry;

import org.springframework.stereotype.Component;
import ru.vsu.tgbot.services.statehandler.bot.BotStateHandler;
import ru.vsu.tgbot.util.BotState;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BotHandlerRegistry {
    private final Map<BotState, BotStateHandler> stateHandlers;

    public BotHandlerRegistry(List<BotStateHandler> stateHandlers) {
        this.stateHandlers = stateHandlers.stream()
                .collect(
                        Collectors.toMap(
                                BotStateHandler::getState,
                                Function.identity()
                        )
                );
    }

    public BotStateHandler getHandler(BotState botState) {
        return stateHandlers.get(botState);
    }
}
