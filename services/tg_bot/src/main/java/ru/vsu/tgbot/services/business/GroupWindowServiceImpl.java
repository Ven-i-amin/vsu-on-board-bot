package ru.vsu.tgbot.services.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.response.GroupResponseDto;
import ru.vsu.tgbot.services.core.GroupService;
import ru.vsu.tgbot.services.session.SessionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupWindowServiceImpl implements GroupWindowService {
    public static final int WINDOW_SIZE = 3;
    private SessionService sessionService;
    private GroupService groupService;

    @Override
    public void moveForward(SessionDto sessionDto, GroupResponseDto newGroup) {
        List<GroupResponseDto> groupPath = sessionDto.getGroupWindow();

        groupPath.removeFirst();
        groupPath.add(newGroup);

        List<GroupResponseDto> updateGroup = new ArrayList<>(List.of(newGroup));

        for (int i = 1; i < WINDOW_SIZE; i++) {
            updateGroup = updateGroup.stream()
                            .map(GroupResponseDto::innerGroups)
                            .flatMap(Collection::stream)
                            .toList();
        }

        List<String> updateGroupId = updateGroup.stream().map(GroupResponseDto::groupId).toList();

        List<GroupResponseDto> uploadedGroups = groupService.getInnerGroupsForEachGroup(
                updateGroupId,
                sessionDto.getLanguage()
        );

        updateGroup.forEach(group -> group.innerGroups().addAll(
                uploadedGroups.stream()
                        .filter(uploadedGroup -> uploadedGroup.parentId().equals(group.groupId()))
                        .toList()
        ));

        sessionService.patchSessionByGroupPath(sessionDto.getChatId(), groupPath);
    }

    @Override
    public void moveBackward(SessionDto sessionDto) {
        List<GroupResponseDto> groupPath = sessionDto.getGroupWindow();

        if (groupPath.size() == 1) {
            return;
        }

        groupPath.removeLast();

        GroupResponseDto parentGroup = groupService.getGroupWithRecursion(
                groupPath.getFirst().parentId(),
                WINDOW_SIZE,
                sessionDto.getLanguage()
        );

        groupPath.addFirst(parentGroup);

        sessionService.patchSessionByGroupPath(sessionDto.getChatId(), groupPath);
    }

    @Override
    public void moveToStart(SessionDto sessionDto) {
        List<GroupResponseDto> groupPath = sessionDto.getGroupWindow();

        groupPath.clear();
        groupPath.add(sessionDto.getStart());

        sessionService.patchSessionByGroupPath(sessionDto.getChatId(), groupPath);
    }
}
