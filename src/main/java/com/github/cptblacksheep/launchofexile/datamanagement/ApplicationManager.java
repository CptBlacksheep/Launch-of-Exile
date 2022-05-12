package com.github.cptblacksheep.launchofexile.datamanagement;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class ApplicationManager {
    private static final String POE_STEAM_ID = "238960";
    private ArrayList<UriWrapper> applications = new ArrayList<>();
    private String poeExeLocation = "";
    private PoeVersion selectedPoeVersion = PoeVersion.STEAM;

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
                        null, "Failed to launch application " + application,
                        "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void startPoe(PoeVersion poeVersion) {
        try {
            switch (poeVersion) {
                case STEAM -> startSteamGameById(POE_STEAM_ID);
                case STANDALONE -> startApplication(poeExeLocation);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null, "Failed to launch Path of Exile",
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

    public String getPoeExeLocation() {
        return poeExeLocation;
    }

    public void setPoeExeLocation(String poeExeLocation) {
        this.poeExeLocation = Objects.requireNonNull(poeExeLocation);
    }

    public PoeVersion getSelectedPoeVersion() {
        return selectedPoeVersion;
    }

    public void setSelectedPoeVersion(PoeVersion selectedPoeVersion) {
        this.selectedPoeVersion = Objects.requireNonNull(selectedPoeVersion);
    }
}
