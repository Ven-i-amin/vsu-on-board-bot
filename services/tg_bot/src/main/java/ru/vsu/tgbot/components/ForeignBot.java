package ru.vsu.tgbot.components;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@Slf4j
@Component
@AllArgsConstructor
public class ForeignBot implements SpringLongPollingBot {
    private final TgUpdateConsumer updateConsumer;

    @Override
    public String getBotToken() {
        return "6436441263:AAFzZGluoG8JbMyQ2GpVkofCmhOemFs-hoo";
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updateConsumer;
    }
}
