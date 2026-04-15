package ru.vsu.apigateway.config.telegram;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class BotConfig {
    @Bean(name = "telegramToken")
    public String telegramToken(BotProperties botProperties) {
        return botProperties.getToken();
    }

    @Bean
    public TelegramClient telegramClient(
        BotProperties botProperties,
        ProxyProperties proxyProperties
    ) {
        if (proxyProperties.isEnabled()) {
            return new OkHttpTelegramClient(buildProxyClient(proxyProperties), botProperties.getToken());
        }

        return new OkHttpTelegramClient(botProperties.getToken());
    }

    @Bean
    public SetWebhook webhook(BotProperties botProperties) {
        String secretToken = StringUtils.hasText(botProperties.getWebhookSecret())
            ? botProperties.getWebhookSecret()
            : botProperties.getToken();

        return SetWebhook.builder()
            .url(botProperties.getWebhookUrl())
            .secretToken(secretToken)
            .build();
    }

    @Bean(name = "usernamePath")
    public String usernamePath(BotProperties botProperties) {
        String webhookPath = botProperties.getWebhookPath();
        if (webhookPath == null || webhookPath.isBlank()) {
            return "/" + botProperties.getUsername();
        }
        return webhookPath.startsWith("/") ? webhookPath : "/" + webhookPath;
    }

    private OkHttpClient buildProxyClient(ProxyProperties proxyProperties) {
        if (proxyProperties.getUsername() != null && proxyProperties.getPassword() != null) {
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
