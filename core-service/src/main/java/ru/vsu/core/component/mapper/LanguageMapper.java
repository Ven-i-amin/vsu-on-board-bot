package ru.vsu.core.component.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import ru.vsu.core.model.dto.LanguageDto;
import ru.vsu.core.model.entity.Language;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    LanguageMapper INSTANCE = Mappers.getMapper(LanguageMapper.class);

    LanguageDto toDto(Language language);

    @Mapping(target = "id", ignore = true)
    Language toEntity(LanguageDto languageDto);

    List<LanguageDto> toDtoList(List<Language> languages);
}
