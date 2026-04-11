package ru.vsu.tgbot.services.business;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.model.dto.LanguageDto;
import ru.vsu.tgbot.services.core.LanguageClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {
    private static final long REFRESH_RETRY_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(30);

    private final LanguageClient languageClient;
    private final List<LanguageDto> languageList = new ArrayList<>();
    private volatile long nextRefreshAttemptAt;

    @PostConstruct
    private void init() {
        refreshLanguages();
    }

    @Override
    public LanguageDto getLanguage(String langCode) {
        refreshLanguagesIfNeeded();
        return languageList.stream()
                .filter(lang -> lang.code().equals(langCode))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<LanguageDto> getLanguages() {
        refreshLanguagesIfNeeded();
        return languageList.stream().toList();
    }

    private void refreshLanguagesIfNeeded() {
        if (!shouldRefresh()) {
            return;
        }

        synchronized (languageList) {
            if (!shouldRefresh()) {
                return;
            }
            refreshLanguages();
        }
    }

    private boolean shouldRefresh() {
        boolean fallbackOnly = languageList.size() == 1 && "ru".equals(languageList.getFirst().code());
        return (languageList.isEmpty() || fallbackOnly) && System.currentTimeMillis() >= nextRefreshAttemptAt;
    }

    private void refreshLanguages() {
        List<LanguageDto> languages = languageClient.getLanguages();
        languageList.clear();
        languageList.addAll(languages);
        nextRefreshAttemptAt = System.currentTimeMillis() + REFRESH_RETRY_DELAY_MILLIS;
    }
}
