package ru.vsu.tgbot.services.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.components.registry.BotHandlerRegistry;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.dto.UserDto;
import ru.vsu.tgbot.services.business.GroupCacheService;
import ru.vsu.tgbot.services.core.UserClient;
import ru.vsu.tgbot.services.session.SessionService;
import ru.vsu.tgbot.services.statehandler.bot.BotStateHandler;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MainMenuState;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.MessageUtil;

@Service
public class QueryServiceImpl implements QueryService {
    private static final Logger log = LoggerFactory.getLogger(QueryServiceImpl.class);
    private final SessionService sessionService;
    private final UserClient userClient;
    private final GroupCacheService groupCacheService;
    private final BotHandlerRegistry botHandlerRegistry;
    private final BotMessageSender botMessageSender;

    public QueryServiceImpl(
            SessionService sessionService,
            UserClient userClient,
            GroupCacheService groupCacheService,
            BotHandlerRegistry botHandlerRegistry,
            BotMessageSender botMessageSender
    ) {
        this.sessionService = sessionService;
        this.userClient = userClient;
        this.groupCacheService = groupCacheService;
        this.botHandlerRegistry = botHandlerRegistry;
        this.botMessageSender = botMessageSender;
    }

    @Override
    public void processQuery(Update update) {
        Long chatId = MessageUtil.extractChatId(update);

        if (chatId == null) {
            return;
        }

        SessionDto sessionDto = getSessionDto(chatId, update);

        try {
            mainLoop(sessionDto);
        } catch (RuntimeException ex) {
            log.warn("Failed to process Telegram update for chat {}", chatId, ex);
            botMessageSender.send(SendMessage.builder()
                    .chatId(chatId)
                    .text("Сервис временно недоступен. Попробуйте позже.")
                    .build());
        }
    }

    @NotNull
    private SessionDto getSessionDto(Long chatId, Update update) {
        SessionDto sessionDto = sessionService.getSession(chatId);

        if (sessionDto == null) {
            UserDto user = userClient.getUser(chatId);
            String language = user == null ? null : user.getLangCode();

            sessionDto = SessionDto.builder()
                    .chatId(chatId)
                    .botState(BotState.SEND)
                    .messageState(MessageState.WELCOME)
                    .globalState(MainMenuState.BLOCK)
                    .groupWindow(new java.util.ArrayList<>())
                    .langCode(language)
                    .build();
        }

        sessionDto.setStart(groupCacheService.getStartGroup());
        sessionDto.setUpdate(update);
        return sessionDto;
    }

    private void mainLoop(SessionDto sessionDto) {
        do {
            BotStateHandler handler = botHandlerRegistry.getHandler(sessionDto.getBotState());
            handler.handle(sessionDto, botMessageSender);

            log.info(sessionDto.toString());
        } while (sessionDto.getBotState() != BotState.LISTEN);

        sessionService.saveSession(sessionDto);
    }
}
