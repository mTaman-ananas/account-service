package com.siriusxi.hexarch.account.adapter.in.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RequiredArgsConstructor
@RestController
@Log4j2
public class TranslationController {

    private static final Map<String, String> PROJECT_KEYS = new HashMap<>(8);
    private static final Map<String, ProjectTrProperties> TRANSLATION_CONFIGURATIONS = new HashMap<>(8);

    @Value(value = "${hexarch.translation.api_keys}")
    private String projectsApiKeys;

    @GetMapping("/translations/keys")
    public String getKeys() {
        return projectsApiKeys;
    }

    @GetMapping("/translations/configurations/")
    public Map<String, ProjectTrProperties> getConfigurations() throws IOException, InterruptedException {
        buildTranslationConfiguration();
        return TRANSLATION_CONFIGURATIONS;
    }

    @GetMapping("/translations/keys/pretty")
    public Map<String, String> getProjectKeys() {
        if (PROJECT_KEYS.isEmpty()) {
            log.info("Building Translation Service Configurations");
            buildProjectKeys();
        }
        return PROJECT_KEYS;
    }

    @GetMapping("/translations/build/all")
    public void buildTranslationFiles() throws IOException, InterruptedException {
        for (var config : TRANSLATION_CONFIGURATIONS.entrySet()) {
            for (Namespace namespace : config.getValue().namespaces()) {
                for (String language : namespace.languages()) {
                    var content = fetchStringContent(getUrl(language, namespace.name(), config.getValue().apiKey()));
                    if (!isEmpty(content)) {
                        downloadTranslationFiles(config.getKey(), language, namespace.name(), content);
                        savePropertiesFileFrom(config.getKey(), language, content);
                    }

                }
            }
        }
    }

    private boolean isEmpty(String content) {
        return content == null ||
                content.isBlank() ||
                content.replace(" ", "").contains("{}");
    }

    /**
     * Here are all Methods used to build dynamic configurations
     */
    private void buildProjectKeys() {
        Arrays.stream(projectsApiKeys
                        .split(";"))
                .map(projectKey -> projectKey
                        .replaceAll("[{}]*", ""))
                .forEach(projectKey -> PROJECT_KEYS
                        .put(projectKey.split(":")[0],
                                projectKey.split(":")[1]));
    }

    private void buildTranslationConfiguration() throws IOException, InterruptedException {
        buildProjectKeys();
        List<Namespace> namespaces;
        List<String> languages;

        for (var projectEntry : PROJECT_KEYS.entrySet()) {
            namespaces = new ArrayList<>();
            for (String namespace : getProjectNamespaces(projectEntry.getValue())) {
                languages = new ArrayList<>(getProjectLanguages(projectEntry.getValue()));
                namespaces.add(new Namespace(namespace, languages));
            }
            TRANSLATION_CONFIGURATIONS.put(projectEntry.getKey(),
                    new ProjectTrProperties(projectEntry.getKey(), projectEntry.getValue(), namespaces));
        }


        for (var config : TRANSLATION_CONFIGURATIONS.entrySet()) {
            log.info("Service Name: {}", config.getKey());
            for (Namespace namespace : config.getValue().namespaces()) {
                for (String language : namespace.languages()) {
                    log.info(getUrl(language, namespace.name(), config.getValue().apiKey()));
                }
                log.info("--------------------------------------------------------------------");
            }
        }
    }

    private void buildTranslationConfiguration2() throws IOException, InterruptedException {
        buildProjectKeys();
        List<Namespace> namespaces;
        List<String> languages;

        for (var projectEntry : PROJECT_KEYS.entrySet()) {
            var content = getProjectVersions(projectEntry.getKey());
//            getFromJasonFor("namespaces", content.getBytes());
//            getFromJasonFor("languages", content.getBytes());

            namespaces = new ArrayList<>();
            for (String namespace : getProjectNamespaces(projectEntry.getValue())) {
                languages = new ArrayList<>(getProjectLanguages(projectEntry.getValue()));
                namespaces.add(new Namespace(namespace, languages));
            }
            TRANSLATION_CONFIGURATIONS.put(projectEntry.getKey(),
                    new ProjectTrProperties(projectEntry.getKey(), projectEntry.getValue(), namespaces));
        }


        for (var config : TRANSLATION_CONFIGURATIONS.entrySet()) {
            log.info("Service Name: {}", config.getKey());
            for (Namespace namespace : config.getValue().namespaces()) {
                for (String language : namespace.languages()) {
                    log.info(getUrl(language, namespace.name(), config.getValue().apiKey()));
                }
                log.info("--------------------------------------------------------------------");
            }
        }
    }

    private String getUrl(String language, String namespace, String apiKey) {
        String base = "https://api.i18nexus.com/project_resources/translations/%s/%s.json?api_key=%s";
        return String.format(base, language, namespace, apiKey);
    }

    private String getFileName(String projectName, String language, String namespace) {
        return String.format("%s_%s_%s.json", projectName, namespace, language);
    }

    private List<String> getProjectNamespaces(String projectsApiKey) throws IOException, InterruptedException {
        var url = String.format("https://api.i18nexus.com/project_resources/namespaces.json?api_key=%s", projectsApiKey);
        return getValuesFromJasonFor("title", fetchContent(url));
    }

    private List<String> getProjectLanguages(String projectsApiKey) throws IOException, InterruptedException {
        var url = String.format("https://api.i18nexus.com/project_resources/languages.json?api_key=%s", projectsApiKey);
        return getValuesFromJasonFor("language_code", fetchContent(url));
    }

    private List<String> getProjectVersions(String projectsApiKey) throws IOException, InterruptedException {
        var url = String.format("https://api.i18nexus.com/project_resources/versions.json?api_key=%s", projectsApiKey);
        return getValuesFromJasonFor("language_code", fetchContent(url));
    }

    private String fetchStringContent(String url) throws IOException, InterruptedException {
        var httpClient = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    private byte[] fetchContent(String url) throws IOException, InterruptedException {
        return fetchStringContent(url).getBytes();
    }

    private List<String> getValuesFromJasonFor(String elementName, byte[] contents) {
        List<String> values = new ArrayList<>(3);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(contents);
            JsonNode collections = rootNode.path("collection");
            Iterator<JsonNode> elements = collections.elements();
            while (elements.hasNext()) {
                JsonNode element = elements.next();
                values.add(element.findValue(elementName).asText());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return values;
    }

    private List<String> getFromJasonFor(String elementName, byte[] contents) {
        List<String> values = new ArrayList<>(3);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode collections = objectMapper.readTree(contents).path("collection");
            values.addAll(Arrays.asList(objectMapper.readValue(collections.findValue(elementName).toString(), String[].class)));
            System.out.println();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return values;
    }

    private void downloadTranslationFiles(String projectName, String language, String namespace, String content) throws IOException, InterruptedException {
        Path directory = Paths.get("translations");

        if (!Files.exists(directory))
            Files.createDirectories(directory);

        Files.writeString(Paths.get(directory.toString(), getFileName(projectName, language, namespace)), content);
    }

    private void savePropertiesFileFrom(String projectName, String language, String json) throws IOException {

        Path directory = Paths.get("messages");

        if (!Files.exists(directory))
            Files.createDirectories(directory);

        String fileName = String.format("%s_%s.properties", projectName, language);
        ObjectMapper om = new ObjectMapper();
        JavaPropsMapper mapper = new JavaPropsMapper();
        Properties props = mapper.writeValueAsProperties(om.readValue(json, Map.class));

        try (FileOutputStream fos = new FileOutputStream(Paths.get(directory.toString(), fileName).toFile())) {
            props.store(fos, "");
        }
    }

    public record Namespace(String name, List<String> languages) {
    }

    public record ProjectTrProperties(
            String projectName,
            String apiKey,
            List<Namespace> namespaces
    ) {
    }

}
