package ru.vsu.tgbot.services.session;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.components.mapper.SessionMapper;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.repository.redis.SessionRepository;

@Service
@AllArgsConstructor
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;

    @Override
    public void saveSession(SessionDto session) {
        sessionRepository.save(SessionMapper.INSTANCE.sessionDtoToSession(session));
    }

    @Override
    public SessionDto getSession(Long chatId) {
        return sessionRepository.findById(chatId)
                .map(SessionMapper.INSTANCE::sessionToSessionDto)
                .orElse(null);
    }

    @Override
    public void deleteSession(Long chatId) {
        sessionRepository.deleteById(chatId);
    }
}
