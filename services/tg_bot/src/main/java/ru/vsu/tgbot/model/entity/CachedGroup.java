package ru.vsu.tgbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.vsu.tgbot.model.dto.GroupDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "cached-groups", timeToLive = CachedGroup.TIME_TO_LIVE)
public class CachedGroup {
    public static final long TIME_TO_LIVE = 3600;

    @Id
    private String groupName;
    private GroupDto group;
}
