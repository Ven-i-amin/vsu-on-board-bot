package ru.vsu.tgbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "groupNav", timeToLive = GroupNavigation.TIME_TO_LIVE)
public class GroupNavigation {
    public static final int TIME_TO_LIVE = 600;

    @Id
    private Long chatId;

    /** Name of the group currently being browsed; null means root/main-menu level. */
    private String currentGroupName;

    /** Name of the group containing the question being viewed. */
    private String questionGroupName;

    /** Name of the question currently being shown. */
    private String currentQuestionName;

    /** Temporary language override when viewing a question in a non-default language. */
    private String questionOverrideLangCode;
}
