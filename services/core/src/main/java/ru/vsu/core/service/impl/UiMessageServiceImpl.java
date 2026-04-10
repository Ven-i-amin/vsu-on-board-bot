package ru.vsu.core.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.contract.model.response.UiMessageResponseDto;
import ru.vsu.core.component.mapper.UiMessageMapper;
import ru.vsu.core.model.dto.UiMessageDto;
import ru.vsu.core.model.entity.UiMessage;
import ru.vsu.core.model.request.UiMessageUpdateRequest;
import ru.vsu.core.repository.UiMessageRepository;
import ru.vsu.core.service.UiMessageService;
import ru.vsu.core.util.LocalizationUtil;

import java.util.List;

@Service
@AllArgsConstructor
public class UiMessageServiceImpl implements UiMessageService {
    private final UiMessageRepository uiMessageRepository;
    private final UiMessageMapper uiMessageMapper;

    @Override
    public List<UiMessageDto> findAll() {
        return uiMessageMapper.toDtoList(uiMessageRepository.findAll());
    }

    @Override
    public boolean existsByName(String name) {
        return uiMessageRepository.existsByName(name);
    }

    @Override
    public UiMessageDto findByName(String name) {
        return uiMessageRepository.findByName(name)
                .map(uiMessageMapper::toDto)
                .orElse(null);
    }

    @Override
    public UiMessageDto save(UiMessageDto uiMessage) {
        return uiMessageMapper.toDto(uiMessageRepository.save(uiMessageMapper.toEntity(uiMessage)));
    }


    @Override
    public UiMessageDto update(String name, UiMessageUpdateRequest uiMessage) {
        if (!LocalizationUtil.hasDefaultLanguage(uiMessage.text())) {
            throw new IllegalArgumentException();
        }

        UiMessage message = uiMessageRepository.findByName(name).orElseThrow(IllegalArgumentException::new);

        message.setText(uiMessage.text());

        return uiMessageMapper.toDto(uiMessageRepository.save(message));
    }

    @Override
    public void deleteByName(String name) {
        uiMessageRepository.deleteByName(name);
    }
}
