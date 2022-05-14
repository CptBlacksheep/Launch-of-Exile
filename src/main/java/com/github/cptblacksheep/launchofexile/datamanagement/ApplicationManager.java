package com.github.cptblacksheep.launchofexile.datamanagement;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class ApplicationManager {
    private static final String POE_STEAM_ID = "238960";
    private ArrayList<UriWrapper> applications = new ArrayList<>();
    @JsonIgnore
    private Settings settings;

    public ApplicationManager(Settings settings) {
        this.settings = Objects.requireNonNull(settings);
    }

    public static void startApplication(String applicationUri) throws IOException {
        new ProcessBuilder(applicationUri).start();
    }

    public static void startApplication(UriWrapper application) throws IOException {
        startApplication(application.getUri());
    }

    public static void startSteamGameById(String id) throws IOException {
        try {
            WebsiteManager.openWebsite("steam://run/" + id); //Uses Steam browser protocol
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
    }

    public void startAllEnabledApplications() {
        applications.stream().filter(UriWrapper::isEnabled).forEach(application -> {
            try {
                startApplication(application);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        null, String.format("Failed to launch application: %s%n%n"
                                + "It's possible that:%n"
                                + "- The path has changed (remove the tool and re add it)%n"
                                + "- The application needs admin rights (set the launcher to start in admin mode, visit the FAQ at https://github.com/CptBlacksheep/Launch-of-Exile for more information)", application.getUri()),
                        "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void startPoe(PoeVersion poeVersion) {
        try {
            switch (poeVersion) {
                case STEAM -> startSteamGameById(POE_STEAM_ID);
                case STANDALONE -> startApplication(settings.getPoeExeLocation());
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null, "Failed to launch Path of Exile\n\n"
                            + "It's possible that:\n"
                            + "- The path has changed (set the new path)\n"
                            + "- The wrong PoE version is selected",
                    "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sortApplications() {
        applications.sort(Comparator.comparing((UriWrapper uriWrapper) -> !uriWrapper.isEnabled())
                .thenComparing(UriWrapper::getName));
    }

    public ArrayList<UriWrapper> getApplications() {
        return applications;
    }

    public void setApplications(ArrayList<UriWrapper> applications) {
        this.applications = Objects.requireNonNull(applications);
    }

    public void addApplication(UriWrapper application) {
        applications.add(Objects.requireNonNull(application));
    }

    public void removeApplication(UriWrapper application) {
        applications.remove(application);
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = Objects.requireNonNull(settings);
    }
}
