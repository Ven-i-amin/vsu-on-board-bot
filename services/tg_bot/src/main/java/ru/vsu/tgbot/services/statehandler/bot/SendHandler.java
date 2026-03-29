package ru.vsu.tgbot.services.statehandler.bot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import ru.vsu.tgbot.components.registry.MessageHandlerRegistry;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.services.statehandler.global.MainMenuHandler;
import ru.vsu.tgbot.services.statehandler.message.MessageStateHandler;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MainMenuState;
import ru.vsu.tgbot.util.UiMessageName;

import static ru.vsu.tgbot.util.MessageUtil.DEFAULT_LANGUAGE_CODE;
import static ru.vsu.tgbot.util.MessageUtil.NOT_FOUND_MESSAGE;

@Service
@AllArgsConstructor
public class SendHandler implements BotStateHandler {
    private MainMenuHandler globalHandler;
    private MessageHandlerRegistry messageRegistry;
    private UiMessageControl uiMessageControl;

    @Override
    public void handle(SessionDto sessionDto, BotMessageSender sender) {
        sessionDto.setBotState(BotState.DELETE);

        if (sessionDto.getGlobalState() == MainMenuState.CREATE) {
            SendMessage mainMenu = globalHandler.create(sessionDto);

            if (mainMenu != null) {
                sender.send(mainMenu);
            }
        }

        MessageStateHandler messageHandler = messageRegistry.getHandler(sessionDto.getMessageState());

        if (messageHandler == null) {
            return;
        }

        try {
            SendMessage message = messageHandler.answer(sessionDto);

            if (message == null) {
                return;
            }

            Message sendedMessage = sender.send(message);
            sessionDto.setLastMessageId(sendedMessage.getMessageId());
        } catch (Exception e) {
            sender.send(createErrorMessage(sessionDto));
        }
    }

    @Override
    public BotState getState() {
        return BotState.SEND;
    }

    private SendMessage createErrorMessage(SessionDto sessionDto) {
        String errorText = uiMessageControl.getUiMessageText(UiMessageName.ERROR, sessionDto.getLangCode());

        if (errorText.equals(NOT_FOUND_MESSAGE)) {
            errorText = uiMessageControl.getUiMessageText(UiMessageName.ERROR, DEFAULT_LANGUAGE_CODE);
        }

        return SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(errorText)
                .build();
    }
}
