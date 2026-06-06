package ru.vsu.tgbot.services.business;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.components.registry.BotHandlerRegistry;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.dto.UserDto;
import ru.vsu.tgbot.services.core.UserService;
import ru.vsu.tgbot.services.session.SessionService;
import ru.vsu.tgbot.services.statehandler.bot.BotStateHandler;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MainMenuState;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.MessageUtil;

@Service
@Slf4j
@AllArgsConstructor
public class QueryServiceImpl implements QueryService {
    private SessionService sessionService;
    private UserService userService;
    private BotHandlerRegistry botHandlerRegistry;
    private BotMessageSender botMessageSender;

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
            UserDto user = userService.getUser(chatId);
            String language = user == null ? null : user.getLangCode();

            sessionDto = SessionDto.builder()
                    .chatId(chatId)
                    .botState(BotState.SEND)
                    .messageState(MessageState.WELCOME)
                    .globalState(MainMenuState.BLOCK)
                    .langCode(language)
                    .build();
        }

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
