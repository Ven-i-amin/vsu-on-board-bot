package ru.vsu.core.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.core.component.mapper.LanguageMapper;
import ru.vsu.core.model.dto.LanguageDto;
import ru.vsu.core.repository.LanguageRepository;
import ru.vsu.core.service.LanguageService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;
    private final Map<String, LanguageDto> languageCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void warmUpCache() {
        languageCache.clear();
        languageRepository.findAll().stream()
                .map(languageMapper::toDto)
                .forEach(language -> languageCache.put(language.code(), language));
    }

    @Override
    public List<LanguageDto> findAll() {
        if (languageCache.isEmpty()) {
            warmUpCache();
        }
        return List.copyOf(languageCache.values());
    }

    @Override
    public LanguageDto findByCode(String code) {
        if (languageCache.isEmpty()) {
            warmUpCache();
        }
        return languageCache.get(code);
    }

    @Override
    public LanguageDto save(LanguageDto language) {
        LanguageDto savedLanguage = languageMapper.toDto(languageRepository.save(languageMapper.toEntity(language)));
        languageCache.put(savedLanguage.code(), savedLanguage);
        return savedLanguage;
    }

    @Override
    public void deleteByCode(String code) {
        languageRepository.deleteById(code);
        languageCache.remove(code);
    }
}
