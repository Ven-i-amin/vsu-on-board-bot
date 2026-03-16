package ru.vsu.tgbot.services.query;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.tgbot.components.StateHandlerRegistry;
import ru.vsu.tgbot.model.SessionInfo;
import ru.vsu.tgbot.model.User;
import ru.vsu.tgbot.services.core.CoreService;
import ru.vsu.tgbot.services.session.SessionService;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MessageState;

import java.util.List;

@Service
@AllArgsConstructor
public class QueryServiceImpl implements QueryService{
    private SessionService sessionService;
    private CoreService coreService;
    private StateHandlerRegistry stateHandler;

    @Override
    public SendMessage processQuery(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        SendMessage sendMessage;
        SessionInfo session = sessionService.getSession(chatId);

        if (session == null) {
            User user = coreService.getUser(chatId);

            if (user == null) {
                sendMessage = newUser(chatId, text);
            } else {
                sendMessage = oldUser(chatId, text);
            }
        } else {
            sendMessage = stateHandler.getHandler(session.getState().getLast()).handle(chatId, text, session);
        }

        return sendMessage;
    }

    private SendMessage newUser(Long chatId, String text) {
        SessionInfo session = SessionInfo
                .builder()
                .messageState(MessageState.ANSWER)
                .state(List.of(BotState.LANGUAGE))
                .build();

        sessionService.saveSession(chatId, session);

        return stateHandler.getHandler(BotState.LANGUAGE).handle(chatId, text, session);
    }

    private SendMessage oldUser(Long chatId, String text) {
        SessionInfo session = SessionInfo
                .builder()
                .messageState(MessageState.ANSWER)
                .state(List.of(BotState.WELCOME))
                .build();

        sessionService.saveSession(chatId, session);

        return stateHandler.getHandler(BotState.WELCOME).handle(chatId, text, session);
    }
}
