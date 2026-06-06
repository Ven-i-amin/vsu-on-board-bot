package ru.vsu.tgbot.services.business;

import java.util.List;

public interface GroupNavigationService {
    /** Returns the name of the group currently being browsed, or null if at root/main-menu. */
    String getCurrentGroupName(Long chatId);

    /** Navigate to a specific group. */
    void setCurrentGroup(Long chatId, String groupName);

    /** Navigate back to the parent using the group's parents list. Null = main menu. */
    void goBack(Long chatId, List<String> parents);

    /** Reset navigation to root (main menu level). */
    void goToRoot(Long chatId);

    /** Record that the user is now viewing a question. */
    void setCurrentQuestion(Long chatId, String questionGroupName, String questionName);

    /** Returns the group name that contains the currently viewed question. */
    String getQuestionGroupName(Long chatId);

    /** Returns the name of the currently viewed question, or null. */
    String getCurrentQuestionName(Long chatId);

    /** Set a temporary language override for question display. */
    void setQuestionOverrideLangCode(Long chatId, String langCode);

    /** Get the current question language override, or null if none. */
    String getQuestionOverrideLangCode(Long chatId);

    /** Clear question state (question + override) while keeping current group. */
    void clearQuestion(Long chatId);

    /** Delete the navigation record entirely. */
    void delete(Long chatId);
}
