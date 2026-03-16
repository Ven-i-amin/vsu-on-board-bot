package ru.vsu.tgbot.components;

import org.springframework.stereotype.Component;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.services.statehandler.StateHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StateHandlerRegistry {
    private final Map<BotState, StateHandler> stateHandlers;

    public StateHandlerRegistry(List<StateHandler> handlers) {
        this.stateHandlers = handlers
                .stream()
                .collect(
                        Collectors.toMap(
                                StateHandler::getState,
                                Function.identity()
                        )
                );
    }

    public StateHandler getHandler(BotState state) {
        return stateHandlers.get(state);
    }
}
