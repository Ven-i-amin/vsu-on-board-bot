package ru.vsu.tgbot.services.business;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.tgbot.components.SessionStateRegistry;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.dto.UserDto;
import ru.vsu.tgbot.services.core.UserService;
import ru.vsu.tgbot.services.session.SessionService;
import ru.vsu.tgbot.services.sessionstate.GlobalMenuHandler;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MessageState;

@Service
@Slf4j
@AllArgsConstructor
public class QueryServiceImpl implements QueryService {
    private SessionService sessionService;
    private UserService userService;
    private SessionStateRegistry stateHandler;
    private GlobalMenuHandler globalMenuHandler;

    private BotMessageSender botMessageSender;

    @Override
    public void processQuery(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        SessionDto sessionDto;

        try {
            sessionDto = sessionService.getSession(chatId);

            if (sessionDto == null) {
                UserDto user = userService.getUser(chatId);

                String language = user == null ? null : user.getLangCode();

                sessionDto = SessionDto
                        .builder()
                        .chatId(chatId)
                        .botState(BotState.SEND)
                        .messageState(MessageState.WELCOME)
                        .langCode(language)
                        .build();

            }

            sessionDto.setText(text);

            do {
                stateHandler
                        .getHandler(sessionDto.getMessageState())
                        .handle(sessionDto, botMessageSender);

                sessionService.saveSession(sessionDto);
            } while (sessionDto.getBotState() == BotState.SEND);
        } catch (RuntimeException ex) {
            log.warn("Failed to process Telegram update for chat {}", chatId, ex);
        }
    }
}
