package ru.vsu.tgbot.config.telegram;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class BotConfig {
    @Bean(name = "telegramToken")
    public String telegramToken(BotProperties botProperties) {
        return botProperties.getToken();
    }

    @Bean
    public TelegramClient telegramClient(BotProperties botProperties, ProxyProperties proxyProperties) {
        if (proxyProperties.isEnabled()) {
            return new OkHttpTelegramClient(buildProxyClient(proxyProperties), botProperties.getToken());
        }

        return new OkHttpTelegramClient(botProperties.getToken());
    }

    @Bean(name = "telegramWebhookPath")
    public String telegramWebhookPath(BotProperties botProperties) {
        String webhookPath = botProperties.getWebhookPath();
        if (!StringUtils.hasText(webhookPath)) {
            return "/telegram/webhook";
        }
        return webhookPath.startsWith("/") ? webhookPath : "/" + webhookPath;
    }

    private OkHttpClient buildProxyClient(ProxyProperties proxyProperties) {
        if (StringUtils.hasText(proxyProperties.getUsername()) && StringUtils.hasText(proxyProperties.getPassword())) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    if (getRequestingHost().equalsIgnoreCase(proxyProperties.getHost())
                        && proxyProperties.getPort() == getRequestingPort()) {
                        return new PasswordAuthentication(
                            proxyProperties.getUsername(),
                            proxyProperties.getPassword().toCharArray()
                        );
                    }
                    return null;
                }
            });
        }

        return new OkHttpClient.Builder()
            .proxy(new Proxy(
                proxyProperties.getType(),
                new InetSocketAddress(proxyProperties.getHost(), proxyProperties.getPort())
            ))
            .build();
    }
}
