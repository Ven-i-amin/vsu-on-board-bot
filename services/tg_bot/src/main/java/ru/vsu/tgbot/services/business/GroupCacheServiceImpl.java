package ru.vsu.tgbot.services.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.entity.CachedGroup;
import ru.vsu.tgbot.repository.CachedGroupRepository;
import ru.vsu.tgbot.services.core.GroupClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupCacheServiceImpl implements GroupCacheService {
    private static final int CACHE_DEPTH = 3;
    private static final String START_GROUP_CACHE_KEY = "start";

    private final CachedGroupRepository cachedGroupRepository;
    private final GroupClient groupClient;

    @Override
    public GroupDto getStartGroup() {
        return getOrLoad(
                START_GROUP_CACHE_KEY,
                () -> groupClient.getStartGroup()
        );
    }

    @Override
    public GroupDto getGroup(String groupName, String language) {
        if (groupName == null || groupName.isBlank()) {
            return null;
        }

        return getOrLoad(
                groupName,
                () -> groupClient.getGroupByNameWithDepth(groupName, CACHE_DEPTH, language)
        );
    }

    @Override
    public List<GroupDto> hydrateGroupPath(List<GroupDto> persistedGroupPath, String language) {
        if (persistedGroupPath == null || persistedGroupPath.isEmpty()) {
            return new ArrayList<>();
        }

        List<GroupDto> hydratedGroupPath = new ArrayList<>(persistedGroupPath.size());

        for (GroupDto group : persistedGroupPath) {
            if (group == null) {
                continue;
            }

            if (group.getName() == null || group.getName().isBlank()) {
                hydratedGroupPath.add(group);
                continue;
            }

            GroupDto cachedGroup = getGroup(group.getName(), language);
            hydratedGroupPath.add(cachedGroup == null ? group : cachedGroup);
        }

        return hydratedGroupPath;
    }

    private GroupDto getOrLoad(String cacheKey, GroupLoader loader) {
        return cachedGroupRepository.findById(cacheKey)
                .map(CachedGroup::getGroup)
                .orElseGet(() -> loadAndCache(cacheKey, loader));
    }

    private GroupDto loadAndCache(String cacheKey, GroupLoader loader) {
        GroupDto group = loader.load();

        if (group != null) {
            cachedGroupRepository.save(CachedGroup.builder()
                    .groupName(cacheKey)
                    .group(group)
                    .build());
        }

        return group;
    }

    @FunctionalInterface
    private interface GroupLoader {
        GroupDto load();
    }
}
