package ru.vsu.tgbot.services.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.model.dto.SessionDto;

@Service
@AllArgsConstructor
public class GroupServiceWithoutTreeImpl implements GroupServiceWithoutTree {
    private GroupCacheService groupCacheService;

    @Override
    public void addGroupToPath(SessionDto sessionDto, String groupName) {
        if (!groupCacheService.groupExists(groupName)) {
            return;
        }

        sessionDto.setActualGroupName(groupName);
    }

    @Override
    public void removeGroupFromPath(SessionDto sessionDto, String groupName) {

    }
}
