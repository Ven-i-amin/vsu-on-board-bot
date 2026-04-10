package ru.vsu.tgbot.util;

import org.apache.commons.lang3.tuple.Pair;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.vsu.tgbot.model.dto.GroupDto;
import ru.vsu.tgbot.model.dto.QuestionDto;
import ru.vsu.tgbot.model.dto.SessionDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class MessageUtil {
    public static String DEFAULT_LANGUAGE_CODE = "ru";
    public static String NOT_FOUND_MESSAGE = "Not Found";

    public static String extractUserInput(Update update) {
        if (update == null) {
            return null;
        }

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQuery == null ? null : callbackQuery.getData();
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            return update.getMessage().getText();
        }

        return null;
    }

    public static Long extractChatId(Update update) {
        if (update == null) {
            return null;
        }

        if (update.hasCallbackQuery()
                && update.getCallbackQuery() != null
                && update.getCallbackQuery().getMessage() != null) {
            return update.getCallbackQuery().getMessage().getChatId();
        }

        if (update.hasMessage() && update.getMessage() != null) {
            return update.getMessage().getChatId();
        }

        return null;
    }

    public static GroupDto getGroupByText(String userText, SessionDto sessionDto) {
        return sessionDto.getStart().getInnerGroups().stream()
                .filter(el -> MessageUtil.isLocalizedGroupTitle(userText, el, sessionDto))
                .findFirst()
                .orElse(null);
    }

    public static GroupDto createGroupForQuestion(QuestionDto question) {
        return GroupDto.builder()
                .title(Map.of())
                .questions(List.of(question))
                .innerGroups(new ArrayList<>())
                .build();
    }

    public static Boolean isLocalizedGroupTitle(String text, GroupDto groupDto, SessionDto sessionDto) {
        return Objects.equals(getLocalizedValue(groupDto.getTitle(), sessionDto.getLangCode()), text);
    }

    public static List<String> getLocalizedGroupTitles(SessionDto sessionDto) {
        List<GroupDto> groups = sessionDto.getStart().getInnerGroups();

        if (groups == null) {
            return List.of();
        }

        return groups.stream()
                .map(group -> getLocalizedValue(group.getTitle(), sessionDto.getLangCode()))
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<Pair<String, String>> getLocalizedGroupNameAndTitles(GroupDto group, SessionDto sessionDto) {
        if (group == null) {
            return List.of();
        }

        return group.getInnerGroups().stream()
                .map(session -> Pair.of(session.getName(), getLocalizedValue(session.getTitle(), sessionDto.getLangCode())))
                .filter(pair -> pair.getRight() != null)
                .toList();
    }

    public static List<String> getLocalizedQuestionTitle(GroupDto group, SessionDto sessionDto) {
        return group.getQuestions().stream()
                .map(QuestionDto::getTitle)
                .map(title -> getLocalizedValue(title, sessionDto.getLangCode()))
                .filter(Objects::nonNull)
                .toList();
    }

    public static List<Pair<String, String>> getLocalizedQuestionNameAndTitles(GroupDto group, SessionDto sessionDto) {
        return group.getQuestions().stream()
                .map(question -> Pair.of(
                        question.getName(),
                        getLocalizedValue(question.getTitle(), sessionDto.getLangCode()))
                )
                .filter(pair -> pair.getRight() != null)
                .toList();
    }

    public static List<InlineKeyboardRow> createInlineButtonColumn(List<Pair<String, String>> namesAndTexts, int rowSize) {
        assert rowSize > 0;

        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        int rowFill = 0;
        InlineKeyboardRow row = new InlineKeyboardRow();

        for (Pair<String, String> nameAndText : namesAndTexts) {
            if (nameAndText == null || nameAndText.getRight() == null || nameAndText.getLeft() == null) {
                continue;
            }

            if (rowFill == rowSize) {
                rowFill = 0;
                keyboardRows.add(row);
                row = new InlineKeyboardRow();
            }

            InlineKeyboardButton button = new InlineKeyboardButton(nameAndText.getRight());
            button.setCallbackData(nameAndText.getLeft());
            row.add(button);

            rowFill++;
        }

        keyboardRows.add(row);

        return keyboardRows;
    }

    public static List<KeyboardRow> createButtonColumn(List<String> texts, int rowSize) {
        assert rowSize > 0;

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        int rowFill = 0;
        KeyboardRow row = new KeyboardRow();

        for (String text : texts) {
            if (text == null) {
                continue;
            }

            if (rowFill == rowSize) {
                rowFill = 0;
                keyboardRows.add(row);
                row = new KeyboardRow();
            }

            KeyboardButton button = new KeyboardButton(text);
            row.add(button);

            rowFill++;
        }

        keyboardRows.add(row);

        return keyboardRows;
    }

    private static String getLocalizedValue(Map<String, String> localizedValues, String langCode) {
        if (localizedValues == null || localizedValues.isEmpty()) {
            return null;
        }

        return Stream.of(
                        langCode,
                        DEFAULT_LANGUAGE_CODE
                )
                .filter(Objects::nonNull)
                .map(localizedValues::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
