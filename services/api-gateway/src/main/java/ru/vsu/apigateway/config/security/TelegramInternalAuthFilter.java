package ru.vsu.apigateway.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import ru.vsu.apigateway.config.telegram.TgBotForwardProperties;

@Component
@RequiredArgsConstructor
public class TelegramInternalAuthFilter implements WebFilter {
    private static final String INTERNAL_TOKEN_HEADER = "X-Internal-Bot-Token";
    private static final String INTERNAL_MESSAGES_PATH = "/internal/telegram/messages";

    private final TgBotForwardProperties properties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!isProtectedPath(exchange)) {
            return chain.filter(exchange);
        }

        String expectedToken = properties.getInternalToken();
        if (!StringUtils.hasText(expectedToken)) {
            return chain.filter(exchange);
        }

        String providedToken = exchange.getRequest().getHeaders().getFirst(INTERNAL_TOKEN_HEADER);
        if (expectedToken.equals(providedToken)) {
            return chain.filter(exchange);
        }

        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    private boolean isProtectedPath(ServerWebExchange exchange) {
        String requestPath = exchange.getRequest().getPath().value();
        return requestPath.equals(INTERNAL_MESSAGES_PATH)
            || requestPath.startsWith(INTERNAL_MESSAGES_PATH + "/");
    }
}
