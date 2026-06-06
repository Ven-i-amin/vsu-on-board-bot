package ru.vsu.tgbot.services.statehandler.message;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaDocument;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.vsu.tgbot.components.bot.BotMessageSender;
import ru.vsu.tgbot.model.dto.LanguageDto;
import ru.vsu.tgbot.model.dto.QuestionDto;
import ru.vsu.tgbot.model.dto.QuestionFileDto;
import ru.vsu.tgbot.model.dto.SessionDto;
import ru.vsu.tgbot.services.business.GroupNavigationService;
import ru.vsu.tgbot.services.business.QuestionService;
import ru.vsu.tgbot.services.business.UiMessageControl;
import ru.vsu.tgbot.services.core.GroupService;
import ru.vsu.tgbot.services.core.LanguageService;
import ru.vsu.tgbot.services.core.QuestionClient;
import ru.vsu.tgbot.util.MessageState;
import ru.vsu.tgbot.util.MessageUtil;
import ru.vsu.tgbot.util.UiMessageName;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class QuestionHandler implements MessageStateHandler {
    public static final int OTHER_LANGUAGE_ROW_SIZE = 1;

    private final GroupNavigationService groupNavigationService;
    private final GroupService groupService;
    private final UiMessageControl uiMessageControl;
    private final QuestionService questionService;
    private final LanguageService languageService;
    private final QuestionClient questionClient;
    private final BotMessageSender sender;

    @Override
    public MessageState getState() {
        return MessageState.QUESTION;
    }

    @Override
    public SendMessage answer(SessionDto sessionDto) {
        QuestionDto question = resolveCurrentQuestion(sessionDto);
        if (question == null) {
            sessionDto.setMessageState(MessageState.NOTHING);
            return null;
        }

        questionService.fixate(question.getName());

        String effectiveLang = resolveEffectiveLang(sessionDto, question);
        String text = question.getText().getOrDefault(effectiveLang, null);

        if (text == null) {
            sessionDto.setMessageState(MessageState.SELECT_OTHER_LANGUAGE);
            return createOtherLanguageMenu(sessionDto, question);
        }

        SendMessage textMessage = SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(text)
                .replyMarkup(createBackButton(sessionDto))
                .build();

        List<QuestionFileDto> files = questionClient.getQuestionFiles(question.getQuestionId());

        if (files.isEmpty()) {
            return textMessage;
        }

        // Send text first, track its ID, then send files together
        Integer messageId = sender.send(textMessage);
        sessionDto.setLastMessageId(messageId);

        sendFiles(sessionDto.getChatId(), files, question.getName());

        return null;
    }

    @Override
    public boolean listen(SessionDto sessionDto) {
        String text = MessageUtil.extractUserInput(sessionDto.getUpdate());
        if (text == null || !text.equals(UiMessageName.BACK.getValue())) return false;

        groupNavigationService.clearQuestion(sessionDto.getChatId());
        sessionDto.setMessageState(MessageState.GROUP);
        return true;
    }

    private void sendFiles(long chatId, List<QuestionFileDto> files, String questionName) {
        if (files.size() == 1) {
            QuestionFileDto file = files.get(0);
            try {
                byte[] bytes = questionClient.getFileContent(file.fileHash());
                sender.sendDocument(SendDocument.builder()
                        .chatId(chatId)
                        .document(new InputFile(new ByteArrayInputStream(bytes), resolveFileName(file)))
                        .build());
            } catch (Exception e) {
                log.warn("Failed to send file '{}' for question '{}'", file.fileHash(), questionName, e);
            }
            return;
        }

        List<InputMediaDocument> media = new ArrayList<>();
        for (QuestionFileDto file : files) {
            try {
                byte[] bytes = questionClient.getFileContent(file.fileHash());
                String name = resolveFileName(file);
                InputMediaDocument doc = InputMediaDocument.builder()
                        .media("attach://" + name)
                        .build();
                doc.setMedia(new ByteArrayInputStream(bytes), name);
                media.add(doc);
            } catch (Exception e) {
                log.warn("Failed to load file '{}' for question '{}'", file.fileHash(), questionName, e);
            }
        }

        if (!media.isEmpty()) {
            sender.sendMediaGroup(SendMediaGroup.builder()
                    .chatId(chatId)
                    .medias(new ArrayList<>(media))
                    .build());
        }
    }

    private String resolveFileName(QuestionFileDto file) {
        return file.fileName() != null && !file.fileName().isBlank()
                ? file.fileName()
                : file.fileHash();
    }

    private QuestionDto resolveCurrentQuestion(SessionDto sessionDto) {
        String questionGroupName = groupNavigationService.getQuestionGroupName(sessionDto.getChatId());
        String questionName = groupNavigationService.getCurrentQuestionName(sessionDto.getChatId());
        if (questionGroupName == null || questionName == null) return null;

        return groupService.getGroupQuestions(questionGroupName).stream()
                .filter(q -> q.getName().equals(questionName))
                .findFirst()
                .orElse(null);
    }

    private String resolveEffectiveLang(SessionDto sessionDto, QuestionDto question) {
        String override = groupNavigationService.getQuestionOverrideLangCode(sessionDto.getChatId());
        if (override != null && question.getText().containsKey(override)) {
            return override;
        }
        return sessionDto.getLangCode();
    }

    private InlineKeyboardMarkup createBackButton(SessionDto sessionDto) {
        List<InlineKeyboardRow> column = MessageUtil.createInlineButtonColumn(
                List.of(uiMessageControl.getUiMessageNameAndText(UiMessageName.BACK, sessionDto.getLangCode())),
                1
        );
        return new InlineKeyboardMarkup(column);
    }

    private SendMessage createOtherLanguageMenu(SessionDto sessionDto, QuestionDto question) {
        String menuText = uiMessageControl.getUiMessageText(
                UiMessageName.OTHER_LANGUAGE_MENU, sessionDto.getLangCode());

        List<LanguageDto> availableLanguages = languageService.getLanguages();
        if (availableLanguages.isEmpty()) return null;

        Set<String> questionKeys = question.getText().keySet();
        List<Pair<String, String>> questionLanguages = availableLanguages.stream()
                .filter(lang -> questionKeys.contains(lang.code()))
                .map(lang -> Pair.of(lang.code(), lang.name().get(lang.code())))
                .toList();

        if (questionLanguages.isEmpty()) return null;

        List<InlineKeyboardRow> buttonRows = MessageUtil.createInlineButtonColumn(
                questionLanguages, OTHER_LANGUAGE_ROW_SIZE);

        return SendMessage.builder()
                .chatId(sessionDto.getChatId())
                .text(menuText)
                .replyMarkup(new InlineKeyboardMarkup(buttonRows))
                .build();
    }
}
