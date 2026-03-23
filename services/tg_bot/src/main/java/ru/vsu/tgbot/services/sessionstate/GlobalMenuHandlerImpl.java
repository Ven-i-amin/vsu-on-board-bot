package ru.vsu.tgbot.services.sessionstate;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.GroupWindowService;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.util.BotState;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.UiMessage;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class GlobalMenuHandlerImpl implements SessionState {
    private UiMessageControl uiMessageControl;
    private GroupWindowService groupWindowService;

    @Override
    public void handle(SessionDto sessionDto, BotMessageSender sender) {
        if (sessionDto.getBotState() == BotState.SEND) {
            sender.send(answer(sessionDto));
        } else {
            listen(sessionDto);
        }
    }

    @Override
    public MessageState getState() {
        return MessageState.MAIN_MENU;
    }

    private SendMessage answer(SessionDto sessionDto) {
        List<KeyboardRow> keyboardRows = MessageUtil.getButtonColumn(
                List.of(UiMessage.LANGUAGE_TITLE.getValue()),
                1
        );

        keyboardRows.addAll(
                MessageUtil.getButtonColumn(MessageUtil.getLocalizedGroupTitles(sessionDto),
                        2
                )
        );

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(keyboardRows);

        return SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(uiMessageControl.getUiMessageText(UiMessage.MAIN_MENU, sessionDto.getLangCode()))
                .replyMarkup(replyKeyboardMarkup)
                .build();
    }

    private void listen(SessionDto sessionDto) {
        sessionDto.setBotState(BotState.SEND);
        String text = sessionDto.getText();

        if (text.equals(uiMessageControl.getUiMessageText(UiMessage.LANGUAGE_TITLE, sessionDto.getLangCode()))) {
            sessionDto.setMessageState(MessageState.LANGUAGE);
            return;
        }

        sessionDto.setMessageState(MessageState.GROUP);
        groupWindowService.moveToStart(sessionDto);
        groupWindowService.moveForward(
                sessionDto,
                MessageUtil.getGroupByText(text, sessionDto)
        );
    }

    private List<String> getTitles(SessionDto sessionDto) {
        List<String> titles = new ArrayList<>();

        titles.add(UiMessage.LANGUAGE_TITLE.getValue());
        titles.addAll(MessageUtil.getLocalizedGroupTitles(sessionDto));

        return titles;
    }
}
