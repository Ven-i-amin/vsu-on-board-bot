package ru.vsu.core.component.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.vsu.core.model.dto.GroupDto;
import ru.vsu.core.model.dto.QuestionDto;
import ru.vsu.core.service.GroupService;
import ru.vsu.core.service.QuestionService;

import java.util.List;
import java.util.Map;

@Component
@Order(40)
@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "app.bootstrap.root-content.enabled",
        havingValue = "true"
)
public class RootContentBootstrap implements ApplicationRunner {
    private static final String DEFAULT_LANGUAGE_CODE = "ru";
    private static final String ENGLISH_LANGUAGE_CODE = "en";

    private final GroupService groupService;
    private final QuestionService questionService;

    @Override
    public void run(ApplicationArguments args) {
        GroupDto rootGroup = groupService.createRootIfMissing();

        GroupDto aboutGroup = ensureGroup(
                rootGroup.groupId(),
                "about-service",
                localizedValue("О сервисе", "About service")
        );
        GroupDto languageGroup = ensureGroup(
                rootGroup.groupId(),
                "language-settings",
                localizedValue("Настройки языка", "Language settings")
        );
        GroupDto studyGroup = ensureGroup(
                rootGroup.groupId(),
                "study-topics",
                localizedValue("Учебные темы", "Study topics")
        );
        GroupDto travelGroup = ensureGroup(
                rootGroup.groupId(),
                "travel-and-life",
                localizedValue("Путешествия и жизнь", "Travel and life")
        );
        GroupDto documentsGroup = ensureGroup(
                rootGroup.groupId(),
                "documents-and-rules",
                localizedValue("Документы и правила", "Documents and rules")
        );

        GroupDto serviceBasicsGroup = ensureGroup(
                aboutGroup.groupId(),
                "service-basics",
                localizedValue("Основы работы", "Service basics")
        );
        GroupDto featuresGroup = ensureGroup(
                aboutGroup.groupId(),
                "service-features",
                localizedValue("Возможности", "Features")
        );
        GroupDto supportGroup = ensureGroup(
                aboutGroup.groupId(),
                "support-and-feedback",
                localizedValue("Поддержка и обратная связь", "Support and feedback")
        );

        GroupDto grammarGroup = ensureGroup(
                studyGroup.groupId(),
                "grammar",
                localizedValue("Грамматика", "Grammar")
        );
        GroupDto vocabularyGroup = ensureGroup(
                studyGroup.groupId(),
                "vocabulary",
                localizedValue("Словарь", "Vocabulary")
        );
        GroupDto speakingGroup = ensureGroup(
                studyGroup.groupId(),
                "speaking-practice",
                localizedValue("Разговорная практика", "Speaking practice")
        );

        GroupDto grammarBasicsGroup = ensureGroup(
                grammarGroup.groupId(),
                "grammar-basics",
                localizedValue("Базовая грамматика", "Basic grammar")
        );
        GroupDto grammarAdvancedGroup = ensureGroup(
                grammarGroup.groupId(),
                "grammar-advanced",
                localizedValue("Сложные конструкции", "Advanced grammar")
        );
        GroupDto everydayVocabularyGroup = ensureGroup(
                vocabularyGroup.groupId(),
                "everyday-vocabulary",
                localizedValue("Повседневная лексика", "Everyday vocabulary")
        );
        GroupDto workVocabularyGroup = ensureGroup(
                vocabularyGroup.groupId(),
                "work-vocabulary",
                localizedValue("Рабочая лексика", "Work vocabulary")
        );
        GroupDto dialoguesGroup = ensureGroup(
                speakingGroup.groupId(),
                "dialogues",
                localizedValue("Диалоги", "Dialogues")
        );
        GroupDto pronunciationGroup = ensureGroup(
                speakingGroup.groupId(),
                "pronunciation",
                localizedValue("Произношение", "Pronunciation")
        );

        GroupDto transportGroup = ensureGroup(
                travelGroup.groupId(),
                "transport",
                localizedValue("Транспорт", "Transport")
        );
        GroupDto housingGroup = ensureGroup(
                travelGroup.groupId(),
                "housing",
                localizedValue("Жильё", "Housing")
        );
        GroupDto cityLifeGroup = ensureGroup(
                travelGroup.groupId(),
                "city-life",
                localizedValue("Городская жизнь", "City life")
        );

        GroupDto visaGroup = ensureGroup(
                documentsGroup.groupId(),
                "visa",
                localizedValue("Виза", "Visa")
        );
        GroupDto residenceGroup = ensureGroup(
                documentsGroup.groupId(),
                "residence",
                localizedValue("Проживание", "Residence")
        );
        GroupDto safetyGroup = ensureGroup(
                documentsGroup.groupId(),
                "safety-rules",
                localizedValue("Правила безопасности", "Safety rules")
        );

        ensureQuestion(
                aboutGroup.name(),
                "how-to-start",
                localizedValue("С чего начать?", "Where to start?"),
                localizedValue(
                        "Откройте нужный раздел в главном меню и выберите интересующий вопрос.",
                        "Open the desired section in the main menu and choose a question you are interested in."
                )
        );
        ensureQuestion(
                serviceBasicsGroup.name(),
                "what-is-this-service",
                localizedValue("Что это за сервис?", "What is this service?"),
                localizedValue(
                        "Это навигатор по темам, вопросам и коротким ответам для пользователя.",
                        "This is a navigator with topics, questions, and short answers for the user."
                )
        );
        ensureQuestion(
                serviceBasicsGroup.name(),
                "how-navigation-works",
                localizedValue("Как устроена навигация?", "How does navigation work?"),
                localizedValue(
                        "Сначала выберите группу, затем подгруппу, а потом конкретный вопрос.",
                        "First choose a group, then a subgroup, and then a specific question."
                )
        );
        ensureQuestion(
                featuresGroup.name(),
                "what-content-is-available",
                localizedValue("Какой контент доступен?", "What content is available?"),
                localizedValue(
                        "В сервисе есть разделы по языку, учёбе, поездкам, документам и бытовым ситуациям.",
                        "The service contains sections about language, study, travel, documents, and everyday situations."
                )
        );
        ensureQuestion(
                supportGroup.name(),
                "how-to-report-problem",
                localizedValue("Как сообщить о проблеме?", "How can I report a problem?"),
                localizedValue(
                        "Если информация неточная или раздела не хватает, передайте обратную связь через ваш основной канал поддержки.",
                        "If some information is inaccurate or a section is missing, send feedback through your main support channel."
                )
        );

        ensureQuestion(
                languageGroup.name(),
                "how-to-change-language",
                localizedValue("Как сменить язык?", "How to change language?"),
                localizedValue(
                        "Нажмите кнопку «Выбрать язык» в главном меню и выберите нужный вариант.",
                        "Press the \"Choose language\" button in the main menu and select the needed option."
                )
        );

        ensureQuestion(
                languageGroup.name(),
                "available-languages",
                localizedValue("Какие языки доступны?", "What languages are available?"),
                localizedValue(
                        "Сейчас предусмотрены русский и английский языки.",
                        "Russian and English are currently available."
                )
        );
        ensureQuestion(
                languageGroup.name(),
                "when-to-switch-language",
                localizedValue("Когда полезно переключать язык?", "When is it useful to switch language?"),
                localizedValue(
                        "Переключайте язык, если вам удобнее читать интерфейс и ответы на другом языке.",
                        "Switch the language when it is more convenient for you to read the interface and answers in another language."
                )
        );

        ensureQuestion(
                studyGroup.name(),
                "how-to-study-here",
                localizedValue("Как использовать разделы для учёбы?", "How can I use these sections for studying?"),
                localizedValue(
                        "Начните с базовой темы, затем переходите к более узким подгруппам и закрепляйте материал вопросами.",
                        "Start with a basic topic, then move to narrower subgroups and reinforce the material with questions."
                )
        );
        ensureQuestion(
                grammarBasicsGroup.name(),
                "what-to-learn-first-in-grammar",
                localizedValue("Что учить в грамматике сначала?", "What should I learn first in grammar?"),
                localizedValue(
                        "Сначала разберите порядок слов, простые времена и базовые вопросы.",
                        "Learn word order, simple tenses, and basic question patterns first."
                )
        );
        ensureQuestion(
                grammarBasicsGroup.name(),
                "how-to-practice-basic-grammar",
                localizedValue("Как тренировать базовую грамматику?", "How to practice basic grammar?"),
                localizedValue(
                        "Составляйте короткие предложения по шаблонам и повторяйте их в разных ситуациях.",
                        "Build short sentences from patterns and repeat them in different situations."
                )
        );
        ensureQuestion(
                grammarAdvancedGroup.name(),
                "when-to-learn-complex-structures",
                localizedValue("Когда переходить к сложным конструкциям?", "When should I move to advanced grammar?"),
                localizedValue(
                        "Переходите к сложным конструкциям после уверенного владения базовыми формами и частыми фразами.",
                        "Move to advanced structures after you are confident with basic forms and common phrases."
                )
        );
        ensureQuestion(
                everydayVocabularyGroup.name(),
                "how-to-expand-everyday-vocabulary",
                localizedValue("Как расширять бытовой словарь?", "How to expand everyday vocabulary?"),
                localizedValue(
                        "Учите слова по темам: дом, магазин, еда, транспорт, здоровье.",
                        "Learn words by themes: home, shopping, food, transport, and health."
                )
        );
        ensureQuestion(
                workVocabularyGroup.name(),
                "which-work-phrases-to-learn",
                localizedValue("Какие рабочие фразы учить в первую очередь?", "Which work phrases should I learn first?"),
                localizedValue(
                        "Начните с приветствия, договорённостей по времени, задач и уточняющих вопросов.",
                        "Start with greetings, scheduling, task wording, and clarification questions."
                )
        );
        ensureQuestion(
                dialoguesGroup.name(),
                "how-to-practice-dialogues",
                localizedValue("Как тренировать диалоги?", "How to practice dialogues?"),
                localizedValue(
                        "Берите короткий сценарий, читайте реплики вслух и меняйте отдельные слова под новую ситуацию.",
                        "Take a short scenario, read the lines aloud, and replace some words for a new situation."
                )
        );
        ensureQuestion(
                pronunciationGroup.name(),
                "how-to-improve-pronunciation",
                localizedValue("Как улучшать произношение?", "How to improve pronunciation?"),
                localizedValue(
                        "Повторяйте короткие фразы за образцом и уделяйте внимание ударению и темпу.",
                        "Repeat short phrases after a model and pay attention to stress and pace."
                )
        );

        ensureQuestion(
                travelGroup.name(),
                "what-to-check-before-trip",
                localizedValue("Что проверить перед поездкой?", "What should I check before a trip?"),
                localizedValue(
                        "Проверьте документы, адрес проживания, маршрут и базовые фразы для дороги.",
                        "Check your documents, accommodation address, route, and basic travel phrases."
                )
        );
        ensureQuestion(
                transportGroup.name(),
                "how-to-ask-for-route",
                localizedValue("Как спросить дорогу или маршрут?", "How can I ask for directions or a route?"),
                localizedValue(
                        "Используйте короткий запрос с названием места и уточните номер транспорта или остановку.",
                        "Use a short request with the place name and clarify the transport number or stop."
                )
        );
        ensureQuestion(
                housingGroup.name(),
                "what-to-clarify-about-housing",
                localizedValue("Что уточнить по жилью?", "What should I clarify about housing?"),
                localizedValue(
                        "Уточните адрес, стоимость, сроки проживания, правила дома и способ связи с владельцем.",
                        "Clarify the address, price, stay dates, house rules, and how to contact the owner."
                )
        );
        ensureQuestion(
                cityLifeGroup.name(),
                "how-to-handle-daily-tasks",
                localizedValue("Как решать бытовые задачи в городе?", "How can I handle daily tasks in the city?"),
                localizedValue(
                        "Подготовьте фразы для магазина, аптеки, банка, доставки и обращения за помощью.",
                        "Prepare phrases for shops, pharmacies, banks, deliveries, and asking for help."
                )
        );

        ensureQuestion(
                documentsGroup.name(),
                "why-documents-matter",
                localizedValue("Почему важно держать документы в порядке?", "Why is it important to keep documents in order?"),
                localizedValue(
                        "Актуальные документы нужны для поездок, проживания, подтверждения личности и обращения в организации.",
                        "Up-to-date documents are needed for travel, residence, identity verification, and dealing with organizations."
                )
        );
        ensureQuestion(
                visaGroup.name(),
                "what-to-check-in-visa",
                localizedValue("Что проверить в визе?", "What should I check in a visa?"),
                localizedValue(
                        "Проверьте сроки действия, тип визы, число въездов и соответствие личных данных.",
                        "Check the validity dates, visa type, number of entries, and that your personal data is correct."
                )
        );
        ensureQuestion(
                residenceGroup.name(),
                "what-to-know-about-registration",
                localizedValue("Что важно знать о регистрации и проживании?", "What is important to know about registration and residence?"),
                localizedValue(
                        "Следите за сроками регистрации, правилами адресного учёта и требованиями местных служб.",
                        "Track registration deadlines, address registration rules, and local service requirements."
                )
        );
        ensureQuestion(
                safetyGroup.name(),
                "what-safety-rules-to-follow",
                localizedValue("Каких правил безопасности придерживаться?", "Which safety rules should I follow?"),
                localizedValue(
                        "Храните копии документов, не передавайте личные данные без необходимости и уточняйте официальные контакты организаций.",
                        "Keep document copies, do not share personal data without need, and verify official organization contacts."
                )
        );
    }

    private GroupDto ensureGroup(String parentGroupId, String name, Map<String, String> title) {
        GroupDto existingGroup = groupService.findByName(name);
        if (existingGroup != null) {
            return existingGroup;
        }

        GroupDto parentGroup = groupService.findById(parentGroupId);
        java.util.List<String> path = parentGroup == null
                ? java.util.List.of()
                : java.util.stream.Stream.concat(
                        (parentGroup.path() == null ? java.util.stream.Stream.<String>empty() : parentGroup.path().stream()),
                        java.util.stream.Stream.of(parentGroup.name())
                )
                .toList();

        return groupService.save(GroupDto.builder()
                .name(name)
                .title(title)
                .path(path)
                .build());
    }

    private void ensureQuestion(String groupName, String name, Map<String, String> title, Map<String, String> text) {
        if (questionService.findByParentGroupNameAndName(groupName, name) != null) {
            return;
        }

        questionService.save(QuestionDto.builder()
                .name(name)
                .parent(groupName)
                .title(title)
                .text(text)
                .build());
    }

    private Map<String, String> localizedValue(String russianValue, String englishValue) {
        return Map.of(
                DEFAULT_LANGUAGE_CODE, russianValue,
                ENGLISH_LANGUAGE_CODE, englishValue
        );
    }
}
