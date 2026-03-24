//package ru.vsu.tgbot.components.bot;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
//import org.telegram.telegrambots.meta.generics.TelegramClient;
//import ru.vsu.tgbot.config.TelegramBotProperties;
//
//@Slf4j
//@Component
//@ConditionalOnProperty(name = "telegram.bot.mode", havingValue = "webhook")
//@RequiredArgsConstructor
//public class TelegramWebhookRegistrar {
//    private final TelegramClient telegramClient;
//    private final TelegramBotProperties properties;
//
//    @EventListener(ApplicationReadyEvent.class)
//    public void registerWebhook() {
//        if (!properties.autoRegister()) {
//            log.info("Telegram webhook auto registration is disabled");
//            return;
//        }
//
//        String webhookUrl = properties.webhookUrl();
//        if (webhookUrl == null || webhookUrl.isBlank()) {
//            log.warn("Telegram webhook URL is empty, skipping registration");
//            return;
//        }
//
//        SetWebhook.SetWebhookBuilder builder = SetWebhook.builder()
//                .url(webhookUrl);
//
//        if (properties.secretToken() != null && !properties.secretToken().isBlank()) {
//            builder.secretToken(properties.secretToken());
//        }
//
//        try {
//            telegramClient.execute(builder.build());
//            log.info("Telegram webhook registered: {}", webhookUrl);
//        } catch (Exception e) {
//            throw new IllegalStateException("Failed to register Telegram webhook", e);
//        }
//    }
//}
