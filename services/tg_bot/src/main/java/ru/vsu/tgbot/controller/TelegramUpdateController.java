package ru.vsu.tgbot.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.tgbot.services.business.QueryService;

@RestController
@RequestMapping("/internal/telegram")
@AllArgsConstructor
public class TelegramUpdateController {
    private final QueryService queryService;
    private final String internalToken;

    @PostMapping("/updates")
    public ResponseEntity<Void> processUpdate(
            @RequestBody Update update,
            @RequestHeader(value = "X-Internal-Bot-Token", required = false) String providedToken
    ) {
        if (StringUtils.hasText(internalToken) && !internalToken.equals(providedToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        queryService.processQuery(update);
        return ResponseEntity.accepted().build();
    }
}
