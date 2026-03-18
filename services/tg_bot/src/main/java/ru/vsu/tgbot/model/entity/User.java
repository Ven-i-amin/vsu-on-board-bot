package ru.vsu.tgbot.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class User {
    @Id
    private Long chatId;
    private String language;
}
