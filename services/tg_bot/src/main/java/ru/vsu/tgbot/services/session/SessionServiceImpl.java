package ru.vsu.tgbot.services.session;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.components.mapper.SessionMapper;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.repository.SessionRepository;
import ru.vsu.tgbot.services.business.GroupCacheService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Service
@AllArgsConstructor
public class SessionServiceImpl implements SessionService {
    private SessionRepository sessionRepository;
    private ExecutorService sessionPatchExecutor;
    private GroupCacheService groupCacheService;

    private final ConcurrentHashMap<Long, GroupPathPatchTask> patchTasksByChatId = new ConcurrentHashMap<>();

    @Override
    public void saveSession(SessionDto session) {
        sessionRepository.save(SessionMapper.INSTANCE.sessionDtoToSession(session));
    }

    @Override
    public SessionDto getSession(Long chatId) {
        return sessionRepository.findById(chatId)
                .map(SessionMapper.INSTANCE::sessionToSessionDto)
                .map(this::hydrateSession)
                .orElse(null);
    }

    @Override
    public void deleteSession(Long chatId) {
        sessionRepository.deleteById(chatId);
    }

    @Override
    public void patchSessionByGroupPath(Long chatId, List<GroupDto> groupPath) {
        List<GroupDto> groupPathSnapshot = sanitizeGroupPath(groupPath);

        GroupPathPatchTask patchTask = patchTasksByChatId.computeIfAbsent(
                chatId,
                ignored -> new GroupPathPatchTask()
        );

        boolean shouldStartWorker;
        synchronized (patchTask) {
            patchTask.latestGroupPath = groupPathSnapshot;
            shouldStartWorker = !patchTask.running;

            if (shouldStartWorker) {
                patchTask.running = true;
            }
        }

        if (shouldStartWorker) {
            sessionPatchExecutor.execute(() -> processLatestGroupPath(chatId, patchTask));
        }
    }

    private void doPatchSessionByGroupPath(Long chatId, List<GroupDto> groupPath) {
        sessionRepository.findById(chatId)
                .ifPresent(session -> {
                    session.setGroupWindow(groupPath);
                    sessionRepository.save(session);
                });
    }

    private void processLatestGroupPath(Long chatId, GroupPathPatchTask patchTask) {
        while (true) {
            List<GroupDto> groupPathToPatch;

            synchronized (patchTask) {
                groupPathToPatch = patchTask.latestGroupPath;
                patchTask.latestGroupPath = null;
            }

            if (groupPathToPatch != null) {
                doPatchSessionByGroupPath(chatId, groupPathToPatch);
            }

            synchronized (patchTask) {
                if (patchTask.latestGroupPath == null) {
                    patchTask.running = false;
                    patchTasksByChatId.remove(chatId, patchTask);
                    return;
                }
            }
        }
    }

    private SessionDto hydrateSession(SessionDto sessionDto) {
        sessionDto.setStart(groupCacheService.getStartGroup());
        sessionDto.setGroupWindow(groupCacheService.hydrateGroupPath(
                sessionDto.getGroupWindow(),
                sessionDto.getLangCode()
        ));
        return sessionDto;
    }

    private List<GroupDto> sanitizeGroupPath(List<GroupDto> groupPath) {
        if (groupPath == null || groupPath.isEmpty()) {
            return List.of();
        }

        return List.copyOf(groupPath.stream()
                .map(this::stripSharedTree)
                .toList());
    }

    private GroupDto stripSharedTree(GroupDto group) {
        if (group == null) {
            return null;
        }

        if (group.getName() == null || group.getName().isBlank()) {
            return group;
        }

        return GroupDto.builder()
                .name(group.getName())
                .title(group.getTitle())
                .parentName(group.getParentName())
                .build();
    }

    private static class GroupPathPatchTask {
        private List<GroupDto> latestGroupPath;
        private boolean running;
    }
}
