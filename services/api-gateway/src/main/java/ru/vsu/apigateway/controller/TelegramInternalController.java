package ru.vsu.apigateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@RestController
@RequestMapping("/internal/telegram/messages")
@RequiredArgsConstructor
public class TelegramInternalController {
    private final TelegramClient telegramClient;

    @PostMapping("/send")
    public ResponseEntity<Message> send(@RequestBody SendMessage message) throws TelegramApiException {
        return ResponseEntity.ok(telegramClient.execute(message));
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@RequestBody DeleteMessage message) throws TelegramApiException {
        telegramClient.execute(message);
        return ResponseEntity.ok().build();
    }
}
