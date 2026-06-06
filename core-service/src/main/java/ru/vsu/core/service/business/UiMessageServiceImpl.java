package ru.vsu.core.service.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.core.component.mapper.UiMessageMapper;
import ru.vsu.core.model.dto.UiMessageDto;
import ru.vsu.core.model.entity.UiMessage;
import ru.vsu.core.model.request.UiMessageUpdateRequest;
import ru.vsu.core.repository.mongo.UiMessageRepository;
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
        UiMessage entity = uiMessageMapper.toEntity(uiMessage);

        if (entity.getDescription() == null && entity.getName() != null) {
            uiMessageRepository.findByName(entity.getName())
                    .map(UiMessage::getDescription)
                    .ifPresent(entity::setDescription);
        }

        return uiMessageMapper.toDto(uiMessageRepository.save(entity));
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
