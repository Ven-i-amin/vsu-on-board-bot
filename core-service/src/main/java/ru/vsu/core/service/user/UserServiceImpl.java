package ru.vsu.core.service.user;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;
import ru.vsu.core.component.mapper.UserMapper;
import ru.vsu.core.model.dto.UserDto;
import ru.vsu.core.model.response.LanguageCountResponse;
import ru.vsu.core.repository.mongo.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MongoTemplate mongoTemplate;

    @Override
    public List<UserDto> findAll() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Override
    public UserDto findByChatId(Long chatId) {
        return userRepository.findById(chatId)
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Override
    public UserDto save(UserDto user) {
        return userMapper.toDto(userRepository.save(userMapper.toEntity(user)));
    }

    @Override
    public List<LanguageCountResponse> getUserLanguageUsage() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("languageCode").count().as("count"),
                Aggregation.lookup("languages", "_id", "code", "languages"),
                Aggregation.project("count")
                        .and("_id").as("languageCode")
                        .andExpression("arrayElemAt(languages.name, 0)").as("name")
                        .andExclude("_id")
        );

        return mongoTemplate.aggregate(aggregation, "users", LanguageCountResponse.class).getMappedResults();
    }

    @Override
    public UserDto updateLangCode(Long chatId, String langCode) {
        UserDto user = findByChatId(chatId);
        if (user == null) {
            user = UserDto.builder()
                    .chatId(chatId)
                    .langCode(langCode)
                    .build();
        } else {
            user.setLangCode(langCode);
        }
        return save(user);
    }

    @Override
    public void deleteByChatId(Long chatId) {
        userRepository.deleteById(chatId);
    }
}
