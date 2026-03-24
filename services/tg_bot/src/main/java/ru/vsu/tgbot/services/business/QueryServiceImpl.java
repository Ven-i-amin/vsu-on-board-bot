package ru.vsu.tgbot.services.business;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.tgbot.components.SessionStateRegistry;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.dto.UserDto;
import ru.vsu.tgbot.services.core.UserService;
import ru.vsu.tgbot.services.session.SessionService;
import ru.vsu.tgbot.services.statehandler.bot.BotStateHandler;
import ru.vsu.tgbot.services.statehandler.global.GlobalStateHandler;
import ru.vsu.tgbot.services.statehandler.message.MessageStateHandler;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.GlobalState;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.MessageUtil;

@Service
@Slf4j
@AllArgsConstructor
public class QueryServiceImpl implements QueryService {
    private SessionService sessionService;
    private UserService userService;
    private SessionStateRegistry stateHandler;
    private BotMessageSender botMessageSender;
    private GlobalStateHandler globalStateHandler;
    private BotStateHandler botStateHandler;

    @Override
    public void processQuery(Update update) {
        Long chatId = MessageUtil.extractChatId(update);
        if (chatId == null) {
            return;
        }

        try {
            Query(chatId, update);
        } catch (RuntimeException ex) {
            log.warn("Failed to process Telegram update for chat {}", chatId, ex);
            botMessageSender.send(SendMessage.builder()
                    .chatId(chatId)
                    .text("Сервис временно недоступен. Попробуйте позже.")
                    .build());
        }
    }

    private void Query(Long chatId, Update update) {
        SessionDto sessionDto = sessionService.getSession(chatId);

        if (sessionDto == null) {
            UserDto user = userService.getUser(chatId);
            String language = user == null ? null : user.getLangCode();

            sessionDto = SessionDto.builder()
                    .chatId(chatId)
                    .botState(BotState.SEND)
                    .messageState(MessageState.WELCOME)
                    .globalState(GlobalState.BLOCK)
                    .groupWindow(new java.util.ArrayList<>())
                    .langCode(language)
                    .build();
        }

        sessionDto.setUpdate(update);

        do {
            botStateHandler.handle(sessionDto, botMessageSender);

            if (globalStateHandler.handle(sessionDto, botMessageSender)
                    && sessionDto.getBotState() == BotState.LISTEN) {
                continue;
            }

            MessageStateHandler messageStateHandler = stateHandler.getHandler(sessionDto.getMessageState());

            if (messageStateHandler != null) {
                messageStateHandler.handle(sessionDto, botMessageSender);
            }

        } while (sessionDto.getBotState() != BotState.LISTEN);

        sessionService.saveSession(sessionDto);
    }
}
