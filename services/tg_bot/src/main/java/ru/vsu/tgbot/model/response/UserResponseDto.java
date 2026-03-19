package ru.vsu.tgbot.model.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class UserResponseDto {
    @Id
    private Long chatId;
    private String language;
}
