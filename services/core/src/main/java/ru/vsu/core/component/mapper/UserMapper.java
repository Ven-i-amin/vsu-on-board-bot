package ru.vsu.core.component.mapper;

import org.mapstruct.Mapper;
import ru.vsu.core.model.dto.UserDto;
import ru.vsu.core.model.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    List<UserDto> toDtoList(List<User> users);
}
