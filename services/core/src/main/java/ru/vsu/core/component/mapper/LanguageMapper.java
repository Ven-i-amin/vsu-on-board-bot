package ru.vsu.core.component.mapper;

import org.mapstruct.Mapper;
import ru.vsu.core.model.dto.LanguageDto;
import ru.vsu.core.model.entity.Language;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    LanguageDto toDto(Language language);

    Language toEntity(LanguageDto languageDto);

    List<LanguageDto> toDtoList(List<Language> languages);
}
