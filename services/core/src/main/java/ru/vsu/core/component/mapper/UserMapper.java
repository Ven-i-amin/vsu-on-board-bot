package ru.vsu.core.component.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.vsu.core.model.dto.UserDto;
import ru.vsu.core.model.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "languageCode", target = "langCode")
    UserDto toDto(User user);

    @Mapping(source = "langCode", target = "languageCode")
    User toEntity(UserDto userDto);

    List<UserDto> toDtoList(List<User> users);
}
