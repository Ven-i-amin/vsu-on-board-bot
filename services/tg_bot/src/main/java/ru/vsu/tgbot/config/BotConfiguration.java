package ru.vsu.tgbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
public class BotConfiguration {
    @Value("webhookUri")
    private String webhookUri;
    @Value("username")
    private String username;
    @Value("token")
    private String token;

    @Bean(name = "telegramToken")
    public String telegramToken() {
        return token;
    }

    @Bean
    public TelegramClient telegramClient() {
        return new OkHttpTelegramClient(token);
    }

    @Bean
    public SetWebhook webhook(){
        return SetWebhook.builder().secretToken(token).url(webhookUri).build();
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService sessionPatchExecutor() {
        return Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));
    }

    @Bean(name = "usernamePath")
    public String usernamePath() {
        return "/" + username;
    }
}
