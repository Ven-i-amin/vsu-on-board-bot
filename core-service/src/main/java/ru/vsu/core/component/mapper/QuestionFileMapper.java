package ru.vsu.core.component.mapper;

import org.mapstruct.Mapper;
import ru.vsu.core.model.dto.QuestionFileDto;
import ru.vsu.core.model.entity.QuestionFile;

@Mapper(componentModel = "spring")
public interface QuestionFileMapper {
    QuestionFileDto toDto(QuestionFile entity);
}
