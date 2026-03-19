package ru.vsu.tgbot.services.session;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.components.mapper.SessionMapper;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.response.GroupResponseDto;
import ru.vsu.tgbot.repository.SessionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Service
@AllArgsConstructor
public class SessionServiceImpl implements SessionService {
    private SessionRepository sessionRepository;
    private ExecutorService sessionPatchExecutor;

    private final ConcurrentHashMap<Long, GroupPathPatchTask> patchTasksByChatId = new ConcurrentHashMap<>();

    @Override
    public void saveSession(SessionDto session) {
        sessionRepository.save(SessionMapper.INSTANCE.sessionDtoToSession(session));
    }

    @Override
    public SessionDto getSession(Long chatId) {
        return sessionRepository.findById(chatId)
                .map(session -> SessionMapper.INSTANCE.sessionToSessionDto(session, null))
                .orElse(null);
    }

    @Override
    public void deleteSession(Long chatId) {
        sessionRepository.deleteById(chatId);
    }

    @Override
    public void patchSessionByGroupPath(Long chatId, List<GroupResponseDto> groupPath) {
        List<GroupResponseDto> groupPathSnapshot = List.copyOf(new ArrayList<>(groupPath));

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

    private void doPatchSessionByGroupPath(Long chatId, List<GroupResponseDto> groupPath) {
        sessionRepository.findById(chatId)
                .ifPresent(session -> {
                    session.setGroupWindow(groupPath);
                    sessionRepository.save(session);
                });
    }

    private void processLatestGroupPath(Long chatId, GroupPathPatchTask patchTask) {
        while (true) {
            List<GroupResponseDto> groupPathToPatch;

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

    private static class GroupPathPatchTask {
        private List<GroupResponseDto> latestGroupPath;
        private boolean running;
    }
}
