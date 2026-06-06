package ru.vsu.core.service.business;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import ru.vsu.core.component.mapper.LanguageMapper;
import ru.vsu.core.model.dto.LanguageDto;
import ru.vsu.core.repository.mongo.LanguageRepository;
import ru.vsu.core.util.LocalizationUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class LanguageServiceImpl implements LanguageService {
    private static final String DEFAULT_LANGUAGE_CODE = LocalizationUtil.DEFAULT_LANGUAGE_CODE;

    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;
    private final MongoTemplate mongoTemplate;
    private final Map<String, LanguageDto> languageCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void warmUpCache() {
        normalizeLanguageDocuments();
        languageCache.clear();
        languageRepository.findAll().stream()
                .map(languageMapper::toDto)
                .forEach(language -> languageCache.put(language.code(), language));
    }

    private void normalizeLanguageDocuments() {
        mongoTemplate.findAll(Document.class, "languages").forEach(languageDocument -> {
            Object name = languageDocument.get("name");
            if (!(name instanceof String legacyName)) {
                return;
            }

            String code = languageDocument.getString("code");
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(languageDocument.getObjectId("_id"))),
                    new Update().set("name", buildLocalizedName(code, legacyName)),
                    "languages"
            );
        });
    }

    private Map<String, String> buildLocalizedName(String code, String legacyName) {
        String englishName = switch (code) {
            case "ru" -> "Russian";
            case "en" -> "English";
            default -> legacyName;
        };

        return Map.of(
                DEFAULT_LANGUAGE_CODE, legacyName,
                "en", englishName
        );
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
        if (!LocalizationUtil.hasDefaultLanguage(language.name())) {
            throw new IllegalArgumentException("Language name must contain russian localization");
        }

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
