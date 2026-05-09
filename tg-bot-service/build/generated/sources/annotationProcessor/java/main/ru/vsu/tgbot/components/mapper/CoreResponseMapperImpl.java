package ru.vsu.tgbot.components.mapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import ru.vsu.contract.model.response.GroupResponseDto;
import ru.vsu.contract.model.response.LanguageResponseDto;
import ru.vsu.contract.model.response.QuestionResponseDto;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.contract.model.response.UserResponseDto;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.LanguageDto;
import ru.vsu.tgbot.model.dto.QuestionDto;
import ru.vsu.tgbot.model.dto.UiMessageDto;
import ru.vsu.tgbot.model.dto.UserDto;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-16T05:47:14+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-9.3.1.jar, environment: Java 21.0.9 (JetBrains s.r.o.)"
)
@Component
public class CoreResponseMapperImpl implements CoreResponseMapper {

    @Override
    public GroupDto toGroupDto(GroupResponseDto groupResponseDto) {
        if ( groupResponseDto == null ) {
            return null;
        }

        GroupDto.GroupDtoBuilder groupDto = GroupDto.builder();

        List<GroupDto> list = toGroupDtoList( groupResponseDto.innerGroups() );
        if ( list != null ) {
            groupDto.innerGroups( list );
        }
        else {
            groupDto.innerGroups( new java.util.ArrayList<>() );
        }
        groupDto.name( groupResponseDto.name() );
        Map<String, String> map = groupResponseDto.title();
        if ( map != null ) {
            groupDto.title( new LinkedHashMap<String, String>( map ) );
        }
        groupDto.parentName( groupResponseDto.parentName() );
        groupDto.questions( toQuestionDtoList( groupResponseDto.questions() ) );

        return groupDto.build();
    }

    @Override
    public List<GroupDto> toGroupDtoList(List<GroupResponseDto> groupResponseDtos) {
        if ( groupResponseDtos == null ) {
            return null;
        }

        List<GroupDto> list = new ArrayList<GroupDto>( groupResponseDtos.size() );
        for ( GroupResponseDto groupResponseDto : groupResponseDtos ) {
            list.add( toGroupDto( groupResponseDto ) );
        }

        return list;
    }

    @Override
    public QuestionDto toQuestionDto(QuestionResponseDto questionResponseDto) {
        if ( questionResponseDto == null ) {
            return null;
        }

        QuestionDto.QuestionDtoBuilder questionDto = QuestionDto.builder();

        questionDto.questionId( questionResponseDto.questionId() );
        questionDto.name( questionResponseDto.name() );
        questionDto.parent( questionResponseDto.parent() );
        Map<String, String> map = questionResponseDto.title();
        if ( map != null ) {
            questionDto.title( new LinkedHashMap<String, String>( map ) );
        }
        Map<String, String> map1 = questionResponseDto.text();
        if ( map1 != null ) {
            questionDto.text( new LinkedHashMap<String, String>( map1 ) );
        }

        return questionDto.build();
    }

    @Override
    public List<QuestionDto> toQuestionDtoList(List<QuestionResponseDto> questionResponseDtos) {
        if ( questionResponseDtos == null ) {
            return null;
        }

        List<QuestionDto> list = new ArrayList<QuestionDto>( questionResponseDtos.size() );
        for ( QuestionResponseDto questionResponseDto : questionResponseDtos ) {
            list.add( toQuestionDto( questionResponseDto ) );
        }

        return list;
    }

    @Override
    public LanguageDto toLanguageDto(LanguageResponseDto languageResponseDto) {
        if ( languageResponseDto == null ) {
            return null;
        }

        String code = null;
        Map<String, String> name = null;

        code = languageResponseDto.code();
        Map<String, String> map = languageResponseDto.name();
        if ( map != null ) {
            name = new LinkedHashMap<String, String>( map );
        }

        LanguageDto languageDto = new LanguageDto( code, name );

        return languageDto;
    }

    @Override
    public List<LanguageDto> toLanguageDtoList(List<LanguageResponseDto> languageResponseDtos) {
        if ( languageResponseDtos == null ) {
            return null;
        }

        List<LanguageDto> list = new ArrayList<LanguageDto>( languageResponseDtos.size() );
        for ( LanguageResponseDto languageResponseDto : languageResponseDtos ) {
            list.add( toLanguageDto( languageResponseDto ) );
        }

        return list;
    }

    @Override
    public UserDto toUserDto(UserResponseDto userResponseDto) {
        if ( userResponseDto == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.langCode( userResponseDto.langCode() );
        userDto.chatId( userResponseDto.chatId() );

        return userDto.build();
    }

    @Override
    public UserResponseDto toUserResponseDto(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        String langCode = null;
        Long chatId = null;

        langCode = userDto.getLangCode();
        chatId = userDto.getChatId();

        UserResponseDto userResponseDto = new UserResponseDto( chatId, langCode );

        return userResponseDto;
    }

    @Override
    public UiMessageDto toUiMessageDto(UiMessageResponseDto uiMessageResponseDto) {
        if ( uiMessageResponseDto == null ) {
            return null;
        }

        UiMessageDto.UiMessageDtoBuilder uiMessageDto = UiMessageDto.builder();

        uiMessageDto.name( uiMessageResponseDto.name() );
        Map<String, String> map = uiMessageResponseDto.text();
        if ( map != null ) {
            uiMessageDto.text( new LinkedHashMap<String, String>( map ) );
        }

        return uiMessageDto.build();
    }

    @Override
    public List<UiMessageDto> toUiMessageDtoList(List<UiMessageResponseDto> uiMessageResponseDtos) {
        if ( uiMessageResponseDtos == null ) {
            return null;
        }

        List<UiMessageDto> list = new ArrayList<UiMessageDto>( uiMessageResponseDtos.size() );
        for ( UiMessageResponseDto uiMessageResponseDto : uiMessageResponseDtos ) {
            list.add( toUiMessageDto( uiMessageResponseDto ) );
        }

        return list;
    }
}
