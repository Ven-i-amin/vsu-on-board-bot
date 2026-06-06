package ru.vsu.core.component.bootstrap;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import ru.vsu.core.model.dto.UiMessageDto;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
@RequiredArgsConstructor
public class StartValueResourceLoader {
    private static final String START_VALUES_RESOURCE_PREFIX = "classpath:startvalues/";
    private static final String ROOT_PARENT_ALIAS = "start-group";
    private static final String UI_MESSAGES_WORKSHEET_ENTRY = "xl/worksheets/sheet1.xml";
    private static final String UI_MESSAGES_SHARED_STRINGS_ENTRY = "xl/sharedStrings.xml";
    private static final Map<String, String> UI_MESSAGE_NAME_MAPPING = Map.of(
            "Назад", "back",
            "Ошибка", "error",
            "Приветствие", "welcome",
            "Главное меню", "main-menu",
            "Выбор языка", "language-title",
            "Какой язык выбрать", "question-answer",
            "Другой язык", "other-language-menu"
    );

    private final ResourceLoader resourceLoader;
    private final JsonParser jsonParser = JsonParserFactory.getJsonParser();

    public List<GroupStartValue> loadGroups() {
        List<Map<String, Object>> rawGroups = readJsonList("groups.txt");
        return rawGroups.stream()
                .map(group -> new GroupStartValue(
                        (String) group.get("name"),
                        castStringMap(group.get("title")),
                        (String) group.get("parentName")
                ))
                .toList();
    }

    public List<QuestionStartValue> loadQuestions() {
        List<Map<String, Object>> rawQuestions = readJsonList("questions.txt");
        return rawQuestions.stream()
                .map(question -> new QuestionStartValue(
                        (String) question.get("name"),
                        (String) question.get("groupName"),
                        castStringMap(question.get("title")),
                        castStringMap(question.get("text"))
                ))
                .toList();
    }

    public List<UiMessageDto> loadUiMessages() {
        try (InputStream inputStream = openResource("uiMessages.xlsx")) {
            Map<String, byte[]> entries = unzipEntries(inputStream, Set.of(
                    UI_MESSAGES_SHARED_STRINGS_ENTRY,
                    UI_MESSAGES_WORKSHEET_ENTRY
            ));

            List<String> sharedStrings = parseSharedStrings(entries.get(UI_MESSAGES_SHARED_STRINGS_ENTRY));
            List<Map<String, String>> sheetRows = parseWorksheet(entries.get(UI_MESSAGES_WORKSHEET_ENTRY), sharedStrings);

            List<UiMessageDto> messages = new ArrayList<>();
            for (Map<String, String> row : sheetRows) {
                String displayName = normalize(row.get("A"));
                if (displayName == null || "Название".equals(displayName)) {
                    continue;
                }

                String internalName = UI_MESSAGE_NAME_MAPPING.get(displayName);
                if (internalName == null) {
                    continue;
                }

                Map<String, String> description = new LinkedHashMap<>();
                putIfPresent(description, "ru", row.get("B"));

                Map<String, String> text = new LinkedHashMap<>();
                putIfPresent(text, "ru", row.get("C"));
                putIfPresent(text, "en", row.get("D"));
                putIfPresent(text, "fr", row.get("E"));
                putIfPresent(text, "es", row.get("F"));
                putIfPresent(text, "zh", row.get("G"));

                if (!text.isEmpty()) {
                    messages.add(UiMessageDto.builder()
                            .name(internalName)
                            .description(description)
                            .text(text)
                            .build());
                }
            }

            messages.add(UiMessageDto.builder()
                    .name("start")
                    .description(Map.of("ru", "Кнопка возврата в главное меню"))
                    .text(Map.of(
                            "ru", "В начало",
                            "en", "Home"
                    ))
                    .build());
            messages.add(UiMessageDto.builder()
                    .name("question-listen")
                    .description(Map.of("ru", "Надпись, которая предлагает выбрать раздел в главном меню"))
                    .text(Map.of(
                            "ru", "Выберите раздел в главном меню.",
                            "en", "Choose a section in the main menu."
                    ))
                    .build());

            return deduplicateByName(messages);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to load start UI messages", exception);
        }
    }

    public boolean isRootParentAlias(String parentName) {
        return Objects.equals(ROOT_PARENT_ALIAS, normalize(parentName));
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> readJsonList(String resourceName) {
        try (InputStream inputStream = openResource(resourceName)) {
            String content = new String(readAllBytes(inputStream), StandardCharsets.UTF_8);
            List<Object> rawList = jsonParser.parseList(content);
            List<Map<String, Object>> result = new ArrayList<>(rawList.size());
            for (Object item : rawList) {
                if (item instanceof Map<?, ?> rawMap) {
                    result.add((Map<String, Object>) rawMap);
                }
            }
            return result;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load start values from " + resourceName, exception);
        }
    }

    private InputStream openResource(String resourceName) throws IOException {
        Resource resource = resourceLoader.getResource(START_VALUES_RESOURCE_PREFIX + resourceName);
        if (!resource.exists()) {
            throw new IllegalStateException("Start values resource is missing: " + resourceName);
        }

        return resource.getInputStream();
    }

    private Map<String, byte[]> unzipEntries(InputStream inputStream, Set<String> expectedEntries) throws IOException {
        Map<String, byte[]> entries = new LinkedHashMap<>();

        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream, StandardCharsets.UTF_8)) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!expectedEntries.contains(zipEntry.getName())) {
                    continue;
                }

                entries.put(zipEntry.getName(), readAllBytes(zipInputStream));
            }
        }

        if (!entries.keySet().containsAll(expectedEntries)) {
            throw new IllegalStateException("uiMessages.xlsx has an unexpected structure");
        }

        return entries;
    }

    private List<String> parseSharedStrings(byte[] xmlBytes) throws Exception {
        var document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new java.io.ByteArrayInputStream(xmlBytes));

        var nodes = document.getElementsByTagNameNS("*", "si");
        List<String> sharedStrings = new ArrayList<>(nodes.getLength());
        for (int index = 0; index < nodes.getLength(); index++) {
            var node = nodes.item(index);
            var textNodes = ((org.w3c.dom.Element) node).getElementsByTagNameNS("*", "t");
            StringBuilder value = new StringBuilder();
            for (int textIndex = 0; textIndex < textNodes.getLength(); textIndex++) {
                value.append(textNodes.item(textIndex).getTextContent());
            }
            sharedStrings.add(value.toString());
        }

        return sharedStrings;
    }

    private List<Map<String, String>> parseWorksheet(byte[] xmlBytes, List<String> sharedStrings) throws Exception {
        var document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new java.io.ByteArrayInputStream(xmlBytes));

        var rowNodes = document.getElementsByTagNameNS("*", "row");
        List<Map<String, String>> rows = new ArrayList<>();

        for (int rowIndex = 0; rowIndex < rowNodes.getLength(); rowIndex++) {
            var rowNode = rowNodes.item(rowIndex);
            var cellNodes = ((org.w3c.dom.Element) rowNode).getElementsByTagNameNS("*", "c");
            Map<String, String> row = new LinkedHashMap<>();

            for (int cellIndex = 0; cellIndex < cellNodes.getLength(); cellIndex++) {
                org.w3c.dom.Element cell = (org.w3c.dom.Element) cellNodes.item(cellIndex);
                String reference = cell.getAttribute("r");
                if (reference == null || reference.isBlank()) {
                    continue;
                }

                String column = reference.replaceAll("\\d", "");
                String value = extractCellValue(cell, sharedStrings);
                if (value != null) {
                    row.put(column, value);
                }
            }

            if (!row.isEmpty()) {
                rows.add(row);
            }
        }

        return rows;
    }

    private String extractCellValue(org.w3c.dom.Element cell, List<String> sharedStrings) {
        var valueNodes = cell.getElementsByTagNameNS("*", "v");
        if (valueNodes.getLength() == 0) {
            return null;
        }

        String rawValue = valueNodes.item(0).getTextContent();
        if ("s".equals(cell.getAttribute("t"))) {
            int sharedStringIndex = Integer.parseInt(rawValue);
            return sharedStrings.get(sharedStringIndex);
        }

        return rawValue;
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        inputStream.transferTo(outputStream);
        return outputStream.toByteArray();
    }

    private static void putIfPresent(Map<String, String> target, String languageCode, String value) {
        Optional.ofNullable(normalize(value))
                .ifPresent(normalizedValue -> target.put(languageCode, normalizedValue));
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private static List<UiMessageDto> deduplicateByName(List<UiMessageDto> messages) {
        Map<String, UiMessageDto> deduplicated = new LinkedHashMap<>();
        for (UiMessageDto message : messages) {
            deduplicated.putIfAbsent(message.name(), message);
        }
        return new ArrayList<>(deduplicated.values());
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> castStringMap(Object rawValue) {
        if (!(rawValue instanceof Map<?, ?> rawMap)) {
            return Map.of();
        }

        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : ((Map<Object, Object>) rawMap).entrySet()) {
            if (entry.getKey() instanceof String key && entry.getValue() instanceof String value) {
                result.put(key, value);
            }
        }
        return result;
    }

    public record GroupStartValue(
            String name,
            Map<String, String> title,
            String parentName
    ) {
    }

    public record QuestionStartValue(
            String name,
            String groupName,
            Map<String, String> title,
            Map<String, String> text
    ) {
    }
}
