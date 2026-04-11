package ru.vsu.tgbot.services.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.session.SessionService;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {
    private SessionService sessionService;
    private GroupCacheService groupCacheService;

    @Override
    public void moveForward(SessionDto sessionDto, GroupDto newGroup) {
        if (newGroup == null) {
            return;
        }

        List<GroupDto> groupPath = sessionDto.getGroupWindow();
        GroupDto hydratedGroup = hydrateGroup(newGroup, sessionDto.getLangCode());

        groupPath.add(hydratedGroup);
        sessionService.patchSessionByGroupPath(sessionDto.getChatId(), groupPath);
    }

    @Override
    public void moveBackward(SessionDto sessionDto) {
        List<GroupDto> groupPath = sessionDto.getGroupWindow();

        if (groupPath.isEmpty()) {
            return;
        }

        groupPath.removeLast();

        sessionService.patchSessionByGroupPath(sessionDto.getChatId(), groupPath);
    }

    @Override
    public void moveToStart(SessionDto sessionDto) {
        List<GroupDto> groupPath = sessionDto.getGroupWindow();

        groupPath.clear();

        sessionService.patchSessionByGroupPath(sessionDto.getChatId(), groupPath);
    }

    @Override
    public void removeLastGroup(SessionDto sessionDto) {
        List<GroupDto> groupPath = sessionDto.getGroupWindow();

        groupPath.removeLast();

        sessionService.patchSessionByGroupPath(sessionDto.getChatId(), groupPath);
    }

    private GroupDto hydrateGroup(GroupDto group, String language) {
        if (group.getName() == null || group.getName().isBlank()) {
            return group;
        }

        GroupDto cachedGroup = groupCacheService.getGroup(group.getName(), language);
        return cachedGroup == null ? group : cachedGroup;
    }
}
