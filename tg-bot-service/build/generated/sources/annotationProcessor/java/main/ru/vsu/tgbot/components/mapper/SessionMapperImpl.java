package ru.vsu.tgbot.components.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.model.entity.Session;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T05:47:14+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 21.0.9 (JetBrains s.r.o.)"
)
public class SessionMapperImpl implements SessionMapper {

    @Override
    public SessionDto sessionToSessionDto(Session session) {
        if ( session == null ) {
            return null;
        }

        SessionDto.SessionDtoBuilder sessionDto = SessionDto.builder();

        sessionDto.chatId( session.getChatId() );
        sessionDto.botState( session.getBotState() );
        sessionDto.messageState( session.getMessageState() );
        sessionDto.globalState( session.getGlobalState() );
        sessionDto.start( session.getStart() );
        List<GroupDto> list = session.getGroupWindow();
        if ( list != null ) {
            sessionDto.groupWindow( new ArrayList<GroupDto>( list ) );
        }
        List<UiMessageResponseDto> list1 = session.getUiMessages();
        if ( list1 != null ) {
            sessionDto.uiMessages( new ArrayList<UiMessageResponseDto>( list1 ) );
        }
        sessionDto.lastMessageId( session.getLastMessageId() );
        sessionDto.langCode( session.getLangCode() );

        return sessionDto.build();
    }

    @Override
    public Session sessionDtoToSession(SessionDto sessionDto) {
        if ( sessionDto == null ) {
            return null;
        }

        Session.SessionBuilder session = Session.builder();

        session.chatId( sessionDto.getChatId() );
        session.botState( sessionDto.getBotState() );
        session.messageState( sessionDto.getMessageState() );
        session.globalState( sessionDto.getGlobalState() );
        session.start( sessionDto.getStart() );
        List<GroupDto> list = sessionDto.getGroupWindow();
        if ( list != null ) {
            session.groupWindow( new ArrayList<GroupDto>( list ) );
        }
        List<UiMessageResponseDto> list1 = sessionDto.getUiMessages();
        if ( list1 != null ) {
            session.uiMessages( new ArrayList<UiMessageResponseDto>( list1 ) );
        }
        session.lastMessageId( sessionDto.getLastMessageId() );
        session.langCode( sessionDto.getLangCode() );

        return session.build();
    }
}
