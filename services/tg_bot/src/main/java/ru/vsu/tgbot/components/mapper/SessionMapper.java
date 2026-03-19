package ru.vsu.tgbot.components.mapper;

import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.entity.Session;

public interface SessionMapper {
    SessionMapper INSTANCE = new SessionMapper() {
    };

    default SessionDto sessionToSessionDto(Session session, String text) {
        if (session == null) {
            return null;
        }

        return SessionDto.builder()
                .chatId(session.getChatId())
                .text(text)
                .botState(session.getBotState())
                .messageState(session.getMessageState())
                .titlePath(session.getGroupWindow())
                .language(session.getLanguage())
                .build();
    }

    default Session sessionDtoToSession(SessionDto sessionDto) {
        if (sessionDto == null) {
            return null;
        }

        return Session.builder()
                .chatId(sessionDto.getChatId())
                .botState(sessionDto.getBotState())
                .messageState(sessionDto.getMessageState())
                .groupWindow(sessionDto.getTitlePath())
                .language(sessionDto.getLanguage())
                .build();
    }
}
