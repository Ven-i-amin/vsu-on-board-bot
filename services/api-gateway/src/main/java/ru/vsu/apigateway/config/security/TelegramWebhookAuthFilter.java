package ru.vsu.apigateway.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import ru.vsu.apigateway.config.telegram.BotProperties;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "telegram.bot.mode", havingValue = "webhook")
public class TelegramWebhookAuthFilter implements WebFilter {
    private static final String TELEGRAM_SECRET_HEADER = "X-Telegram-Bot-Api-Secret-Token";

    private final BotProperties botProperties;
    @Qualifier("usernamePath")
    private final String usernamePath;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!isWebhookRequest(exchange)) {
            return chain.filter(exchange);
        }

        String expectedSecret = botProperties.getWebhookSecret();
        if (!StringUtils.hasText(expectedSecret)) {
            return chain.filter(exchange);
        }

        String providedSecret = exchange.getRequest().getHeaders().getFirst(TELEGRAM_SECRET_HEADER);
        if (expectedSecret.equals(providedSecret)) {
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private boolean isWebhookRequest(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value().equals(normalizePath(usernamePath));
    }

    private String normalizePath(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }
}
