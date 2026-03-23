package ru.vsu.core.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.core.component.mapper.UiMessageMapper;
import ru.vsu.core.model.dto.UiMessageDto;
import ru.vsu.core.repository.UiMessageRepository;
import ru.vsu.core.service.UiMessageService;

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
    public void deleteByName(String name) {
        uiMessageRepository.deleteByName(name);
    }
}
