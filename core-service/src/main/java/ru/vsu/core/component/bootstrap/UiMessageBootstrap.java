package ru.vsu.core.component.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.vsu.core.model.dto.UiMessageDto;
import ru.vsu.core.service.UiMessageService;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(30)
@RequiredArgsConstructor
public class UiMessageBootstrap implements ApplicationRunner {
    private final UiMessageService uiMessageService;

    @Override
    public void run(ApplicationArguments args) {
        defaultMessages().forEach(this::ensureExists);
    }

    private void ensureExists(UiMessageDto message) {
        UiMessageDto existingMessage = uiMessageService.findByName(message.name());
        if (existingMessage == null) {
            uiMessageService.save(message);
            return;
        }

        if ((existingMessage.description() == null || existingMessage.description().isEmpty())
                && message.description() != null
                && !message.description().isEmpty()) {
            uiMessageService.save(UiMessageDto.builder()
                    .id(existingMessage.id())
                    .name(existingMessage.name())
                    .description(message.description())
                    .text(existingMessage.text())
                    .build());
        }
    }

    private List<UiMessageDto> defaultMessages() {
        return List.of(
                uiMessage(
                        "back",
                        "Кнопка \"Назад\"",
                        Map.of(
                                "ru", "Назад",
                                "en", "Back",
                                "fr", "Retour",
                                "es", "Atrás",
                                "zh", "返回"
                        )
                ),
                uiMessage(
                        "start",
                        "Кнопка возврата в главное меню",
                        Map.of(
                                "ru", "В начало",
                                "en", "Home"
                        )
                ),
                uiMessage(
                        "error",
                        "Если возникли проблемы с ботом",
                        Map.of(
                                "ru", "Возникла проблема. Попробуйте позже",
                                "en", "A problem has occurred. Please try again later.",
                                "fr", "Un problème est survenu. Veuillez réessayer plus tard.",
                                "es", "Ha ocurrido un problema. Inténtelo de nuevo más tarde.",
                                "zh", "出现问题，请稍后重试。"
                        )
                ),
                uiMessage(
                        "welcome",
                        "Когда пользователь только зашел в бот",
                        Map.of(
                                "ru", "Добро пожаловать!",
                                "en", "Welcome!",
                                "fr", "Bienvenue !",
                                "es", "¡Bienvenido!",
                                "zh", "欢迎！"
                        )
                ),
                uiMessage(
                        "main-menu",
                        "Надпись, которая обозначает, что пользователь находиться в главном меню",
                        Map.of(
                                "ru", "Главное меню",
                                "en", "Main menu",
                                "fr", "Menu principal",
                                "es", "Menú principal",
                                "zh", "主菜单"
                        )
                ),
                uiMessage(
                        "language-title",
                        "Кнопка смена языка",
                        Map.of(
                                "ru", "Выбрать язык",
                                "en", "Select language",
                                "fr", "Choisir la langue",
                                "es", "Seleccionar idioma",
                                "zh", "选择语言"
                        )
                ),
                uiMessage(
                        "question-listen",
                        "Надпись, которая предлагает выбрать раздел в главном меню",
                        Map.of(
                                "ru", "Выберите раздел в главном меню.",
                                "en", "Choose a section in the main menu."
                        )
                ),
                uiMessage(
                        "question-answer",
                        "Надпись, которая предлагает выбрать язык",
                        Map.of(
                                "ru", "Выберите язык:",
                                "en", "Select language:",
                                "fr", "Choisissez la langue :",
                                "es", "Seleccione el idioma:",
                                "zh", "请选择语言："
                        )
                ),
                uiMessage(
                        "other-language-menu",
                        "Надпись, которая предлагает выбрать другой язык, если вопрос не переведен на язык пользователя.",
                        Map.of(
                                "ru", "На вашем языке данного вопроса еще нет. Вопрос можно посмотреть на данных языках:",
                                "en", "This question is not yet available in your language. You can view the question in the following languages:",
                                "fr", "Cette question n'est pas encore disponible dans votre langue. Vous pouvez consulter la question dans les langues suivantes :",
                                "es", "Esta pregunta aún no está disponible en su idioma. Puede ver la pregunta en los siguientes idiomas:",
                                "zh", "您选择的语言尚无此问题。您可以通过以下语言查看问题："
                        )
                )
        );
    }

    private UiMessageDto uiMessage(String name, String descriptionRu, Map<String, String> text) {
        return UiMessageDto.builder()
                .name(name)
                .description(Map.of("ru", descriptionRu))
                .text(text)
                .build();
    }
}
