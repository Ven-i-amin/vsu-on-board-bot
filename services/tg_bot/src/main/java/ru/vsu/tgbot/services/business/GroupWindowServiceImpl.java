package ru.vsu.tgbot.services.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.core.GroupService;
import ru.vsu.tgbot.services.session.SessionService;
import ru.vsu.tgbot.util.UiMessageName;

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
    public void moveForward(SessionDto sessionDto, GroupDto newGroup) {
        if (newGroup == null) {
            return;
        }

        List<GroupDto> groupPath = sessionDto.getGroupWindow();
        if (groupPath.isEmpty()) {
            groupPath.add(newGroup);
            sessionService.patchSessionByGroupPath(sessionDto.getChatId(), groupPath);
            return;
        }

        groupPath.add(newGroup);
        if (groupPath.size() > WINDOW_SIZE) {
            groupPath.removeFirst();
        }


        List<GroupDto> updateGroup = new ArrayList<>(List.of(newGroup));

        for (int i = 1; i < WINDOW_SIZE; i++) {
            updateGroup = updateGroup.stream()
                            .map(GroupDto::getInnerGroups)
                            .flatMap(Collection::stream)
                            .toList();
        }

        List<String> updateGroupNames = updateGroup.stream().map(GroupDto::getName).toList();

        List<GroupDto> uploadedGroups = groupService.getInnerGroupsForEachGroup(
                updateGroupNames,
                sessionDto.getLangCode()
        );

        updateGroup.forEach(group -> {
            if (group.getInnerGroups() == null) {
                return;
            }
            group.getInnerGroups().addAll(
                    uploadedGroups.stream()
                            .filter(uploadedGroup -> uploadedGroup.getParentName().equals(group.getName()))
                            .toList()
            );
        });

        sessionService.patchSessionByGroupPath(sessionDto.getChatId(), groupPath);
    }

    @Override
    public void moveBackward(SessionDto sessionDto) {
        List<GroupDto> groupPath = sessionDto.getGroupWindow();

        if (groupPath.isEmpty()) {
            return;
        }

        groupPath.removeLast();

        if (groupPath.isEmpty()) {
            sessionService.patchSessionByGroupPath(sessionDto.getChatId(), groupPath);
            return;
        }

        String parentName = groupPath.getFirst().getParentName();

        if (parentName == null || parentName.equals(UiMessageName.MAIN_MENU.getValue())) {
            return;
        }

        GroupDto parentGroup = groupService.getGroupByNameWithDepth(
                parentName,
                WINDOW_SIZE,
                sessionDto.getLangCode()
        );

        if (parentGroup == null) {
            return;
        }

        groupPath.addFirst(parentGroup);

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
}
