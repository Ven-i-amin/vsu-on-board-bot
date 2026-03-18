package ru.vsu.tgbot.components;

import org.springframework.stereotype.Component;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.services.sessionstate.SessionState;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SessionStateRegistry {
    private final Map<BotState, SessionState> stateHandlers;

    public SessionStateRegistry(List<SessionState> handlers) {
        this.stateHandlers = handlers
                .stream()
                .collect(
                        Collectors.toMap(
                                SessionState::getState,
                                Function.identity()
                        )
                );
    }

    public SessionState getHandler(BotState state) {
        return stateHandlers.get(state);
    }
}
