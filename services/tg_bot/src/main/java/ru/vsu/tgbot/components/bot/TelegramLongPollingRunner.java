package ru.vsu.tgbot.components.bot;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.vsu.tgbot.config.TelegramBotProperties;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Slf4j
@Component
@ConditionalOnProperty(name = "telegram.bot.mode", havingValue = "long-polling")
@RequiredArgsConstructor
public class TelegramLongPollingRunner {
    private final TelegramClient telegramClient;
    private final TgUpdateConsumer updateConsumer;
    private final TelegramBotProperties properties;
    private final ExecutorService sessionPatchExecutor;

    private volatile boolean running;
    private volatile Integer offset;
    private Future<?> pollingTask;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        running = true;
        pollingTask = sessionPatchExecutor.submit(this::pollLoop);
        log.info("Telegram long polling started");
    }

    @PreDestroy
    public void stop() {
        running = false;

        if (pollingTask != null) {
            pollingTask.cancel(true);
        }
    }

    private void pollLoop() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                List<Update> updates = telegramClient.execute(GetUpdates.builder()
                        .offset(offset)
                        .timeout(properties.pollingTimeoutSeconds())
                        .build());

                for (Update update : updates) {
                    offset = update.getUpdateId() + 1;
                    updateConsumer.consume(update);
                }
            } catch (Exception e) {
                log.warn("Telegram long polling request failed", e);
                sleepBackoff();
            }
        }
    }

    private void sleepBackoff() {
        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
