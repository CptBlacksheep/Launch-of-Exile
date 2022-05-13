package com.github.cptblacksheep.launchofexile.datamanagement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

public class JsonSerializer {
    public static final String FOLDER_PATH = System.getProperty("user.home") + "\\AppData\\Local\\Launch of Exile\\";
    public static final String SETTINGS_PATH = FOLDER_PATH + "settings.json";
    public static final String DATA_PATH = FOLDER_PATH + "data.json";
    @JsonUnwrapped
    private ApplicationManager applicationManager;
    @JsonUnwrapped
    private WebsiteManager websiteManager;
    @JsonIgnore
    private Settings settings;

    public JsonSerializer(ApplicationManager applicationManager, WebsiteManager websiteManager, Settings settings) {
        this.applicationManager = Objects.requireNonNull(applicationManager);
        this.websiteManager = Objects.requireNonNull(websiteManager);
        this.settings = settings;
    }

    public void saveData() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter prettyWriter = objectMapper.writerWithDefaultPrettyPrinter();

        try {
            prettyWriter.writeValue(Path.of(DATA_PATH).toFile(), this);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to save data",
                    "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void loadData() {
        File file = Path.of(DATA_PATH).toFile();

        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode jsonNode = mapper.readTree(file);

            JsonNode part = jsonNode.path("applications");
            part.forEach(subNode -> {
                try {
                    UriWrapper application = mapper.treeToValue(subNode, UriWrapper.class);
                    applicationManager.addApplication(application);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });

            part = jsonNode.path("websites");
            part.forEach(subNode -> {
                try {
                    UriWrapper website = mapper.treeToValue(subNode, UriWrapper.class);
                    websiteManager.addWebsite(website);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException | RuntimeException ex) {
            JOptionPane.showMessageDialog(null, "Failed to load data",
                    "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
        }

        applicationManager.sortApplications();
        websiteManager.sortWebsites();
    }

    public void saveSettings() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter prettyWriter = objectMapper.writerWithDefaultPrettyPrinter();

        try {
            prettyWriter.writeValue(Path.of(SETTINGS_PATH).toFile(), settings);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to save settings",
                    "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadSettings() {
        File file = Path.of(SETTINGS_PATH).toFile();

        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode jsonNode = mapper.readTree(file);

            JsonNode part = jsonNode.get("poeExeLocation");
            String poeExeLocation = part.textValue();
            settings.setPoeExeLocation(poeExeLocation);

            part = jsonNode.get("selectedPoeVersion");
            PoeVersion poeVersion = mapper.convertValue(part.textValue(), PoeVersion.class);
            settings.setSelectedPoeVersion(poeVersion);

            part = jsonNode.get("darkModeEnabled");

            boolean darkMode = part.booleanValue();
            settings.setDarkModeEnabled(darkMode);

        } catch (IOException | RuntimeException ex) {
            JOptionPane.showMessageDialog(null, "Failed to load settings",
                    "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void saveDataAndSettings() {
        saveData();
        saveSettings();
    }

    public void loadDataAndSettings() {
        loadData();
        loadSettings();
    }

    public ApplicationManager getApplicationManager() {
        return applicationManager;
    }

    public void setApplicationManager(ApplicationManager applicationManager) {
        this.applicationManager = Objects.requireNonNull(applicationManager);
    }

    public WebsiteManager getWebsiteManager() {
        return websiteManager;
    }

    public void setWebsiteManager(WebsiteManager websiteManager) {
        this.websiteManager = Objects.requireNonNull(websiteManager);
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = Objects.requireNonNull(settings);
    }
}
