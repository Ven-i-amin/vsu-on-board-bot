package ru.vsu.tgbot.services.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.tgbot.model.entity.GroupNavigation;
import ru.vsu.tgbot.repository.redis.GroupNavigationRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class GroupNavigationServiceImpl implements GroupNavigationService {
    private final GroupNavigationRepository groupNavigationRepository;

    @Override
    public String getCurrentGroupName(Long chatId) {
        return groupNavigationRepository.findById(chatId)
                .map(GroupNavigation::getCurrentGroupName)
                .orElse(null);
    }

    @Override
    public void setCurrentGroup(Long chatId, String groupName) {
        GroupNavigation nav = getOrCreate(chatId);
        nav.setCurrentGroupName(groupName);
        nav.setCurrentQuestionName(null);
        nav.setQuestionGroupName(null);
        nav.setQuestionOverrideLangCode(null);
        groupNavigationRepository.save(nav);
    }

    @Override
    public void goBack(Long chatId, List<String> parents) {
        String parentName = (parents != null && !parents.isEmpty())
                ? parents.get(parents.size() - 1)
                : null;
        setCurrentGroup(chatId, parentName);
    }

    @Override
    public void goToRoot(Long chatId) {
        setCurrentGroup(chatId, null);
    }

    @Override
    public void setCurrentQuestion(Long chatId, String questionGroupName, String questionName) {
        GroupNavigation nav = getOrCreate(chatId);
        nav.setQuestionGroupName(questionGroupName);
        nav.setCurrentQuestionName(questionName);
        nav.setQuestionOverrideLangCode(null);
        groupNavigationRepository.save(nav);
    }

    @Override
    public String getQuestionGroupName(Long chatId) {
        return groupNavigationRepository.findById(chatId)
                .map(GroupNavigation::getQuestionGroupName)
                .orElse(null);
    }

    @Override
    public String getCurrentQuestionName(Long chatId) {
        return groupNavigationRepository.findById(chatId)
                .map(GroupNavigation::getCurrentQuestionName)
                .orElse(null);
    }

    @Override
    public void setQuestionOverrideLangCode(Long chatId, String langCode) {
        GroupNavigation nav = getOrCreate(chatId);
        nav.setQuestionOverrideLangCode(langCode);
        groupNavigationRepository.save(nav);
    }

    @Override
    public String getQuestionOverrideLangCode(Long chatId) {
        return groupNavigationRepository.findById(chatId)
                .map(GroupNavigation::getQuestionOverrideLangCode)
                .orElse(null);
    }

    @Override
    public void clearQuestion(Long chatId) {
        GroupNavigation nav = getOrCreate(chatId);
        nav.setCurrentQuestionName(null);
        nav.setQuestionGroupName(null);
        nav.setQuestionOverrideLangCode(null);
        groupNavigationRepository.save(nav);
    }

    @Override
    public void delete(Long chatId) {
        groupNavigationRepository.deleteById(chatId);
    }

    private GroupNavigation getOrCreate(Long chatId) {
        return groupNavigationRepository.findById(chatId)
                .orElse(GroupNavigation.builder().chatId(chatId).build());
    }
}
