package ru.vsu.tgbot.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import ru.vsu.tgbot.model.dto.GroupDto;

@RedisHash(value = "cached-groups", timeToLive = CachedGroup.TIME_TO_LIVE)
public class CachedGroup {
    public static final long TIME_TO_LIVE = 3600;

    @Id
    private String groupName;
    private GroupDto group;

    public CachedGroup() {
    }

    public CachedGroup(String groupName, GroupDto group) {
        this.groupName = groupName;
        this.group = group;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public GroupDto getGroup() {
        return group;
    }

    public void setGroup(GroupDto group) {
        this.group = group;
    }

    public static CachedGroupBuilder builder() {
        return new CachedGroupBuilder();
    }

    public static class CachedGroupBuilder {
        private String groupName;
        private GroupDto group;

        public CachedGroupBuilder groupName(String groupName) {
            this.groupName = groupName;
            return this;
        }

        public CachedGroupBuilder group(GroupDto group) {
            this.group = group;
            return this;
        }

        public CachedGroup build() {
            return new CachedGroup(groupName, group);
        }
    }
}
