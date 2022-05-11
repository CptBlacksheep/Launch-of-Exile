package com.github.cptblacksheep.launchofexile;

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
    private static final String FILENAME = "data.json";
    private static final String FILEPATH =
            System.getProperty("user.home") + "\\AppData\\Local\\Launch of Exile\\" + FILENAME;

    @JsonUnwrapped
    private ApplicationManager applicationManager;
    @JsonUnwrapped
    private WebsiteManager websiteManager;

    public JsonSerializer(ApplicationManager applicationManager, WebsiteManager websiteManager) {
        this.applicationManager = Objects.requireNonNull(applicationManager);
        this.websiteManager = Objects.requireNonNull(websiteManager);
    }

    public void saveData() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter prettyWriter = objectMapper.writerWithDefaultPrettyPrinter();

        try {
            prettyWriter.writeValue(Path.of(FILEPATH).toFile(), this);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to save data",
                    "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    public void loadData() {
        File file = Path.of(FILEPATH).toFile();

        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode jsonNode = mapper.readTree(file);

            JsonNode part = jsonNode.get("poeExeLocation");
            applicationManager.setPoeExeLocation(part.asText(""));

            part = jsonNode.get("selectedPoeVersion");
            PoeVersion poeVersion = mapper.convertValue(part.asText("STEAM"), PoeVersion.class);
            applicationManager.setSelectedPoeVersion(poeVersion);

            part = jsonNode.path("applications");
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

}
