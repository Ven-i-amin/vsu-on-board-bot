package ru.vsu.tgbot.components.registry;

import org.springframework.stereotype.Component;
import ru.vsu.tgbot.services.statehandler.message.MessageStateHandler;
import ru.vsu.tgbot.util.MessageState;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MessageHandlerRegistry {
    private final Map<MessageState, MessageStateHandler> stateHandlers;

    public MessageHandlerRegistry(List<MessageStateHandler> handlers) {
        this.stateHandlers = handlers.stream()
                .collect(
                        Collectors.toMap(
                                MessageStateHandler::getState,
                                Function.identity()
                        )
                );
    }

    public MessageStateHandler getHandler(MessageState state) {
        return stateHandlers.get(state);
    }
}
