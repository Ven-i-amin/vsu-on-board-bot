package ru.vsu.tgbot.services.session;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.model.entity.Session;

import java.time.Duration;

@Service
@AllArgsConstructor
public class SessionServiceImpl implements SessionService {
    public static final int SESSION_MINUTE_DURATION = 10;
    private final RedisTemplate<Long, Session> redisTemplate;

    @Override
    public void saveSession(Long chatId, Session session) {
        redisTemplate.opsForValue().set(
                chatId,
                session,
                Duration.ofMinutes(SESSION_MINUTE_DURATION)
        );
    }

    @Override
    public Session getSession(Long chatId) {
        return redisTemplate.opsForValue().get(chatId);
    }
}
