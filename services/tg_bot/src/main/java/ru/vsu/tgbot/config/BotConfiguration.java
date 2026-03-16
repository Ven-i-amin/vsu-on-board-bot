package ru.vsu.tgbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class BotConfiguration {
    @Bean(name = "botToken")
    public String botToken(@Value("${telegram.bot.token}") String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("telegram.bot.token is not configured");
        }
        return token;
    }

    @Bean
    public TelegramClient telegramClient(@Value("${telegram.bot.token}") String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("telegram.bot.token is not configured");
        }
        return new OkHttpTelegramClient(token);
    }
}
