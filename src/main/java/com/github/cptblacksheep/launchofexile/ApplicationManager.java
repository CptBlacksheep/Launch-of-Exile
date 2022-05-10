package com.github.cptblacksheep.launchofexile;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class ApplicationManager {
    private static final String POE_STEAM_ID = "238960";
    private ArrayList<String> applications = new ArrayList<>();
    private String poeExeLocation = "";
    private PoeVersion selectedPoeVersion = PoeVersion.STEAM;

    public static void startApplication(String applicationPath) throws IOException {
        new ProcessBuilder(applicationPath).start();
    }

    public static void startSteamGameById(String id) throws IOException {
        try {
            WebsiteManager.openWebsite("steam://run/" + id); //Uses Steam browser protocol
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
    }

    public void startAllApplications() {
        applications.forEach(application -> {
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

    public ArrayList<String> getApplications() {
        return applications;
    }

    public void setApplications(ArrayList<String> applications) {
        this.applications = applications;
    }

    public void addApplication(String applicationPath) {
        applications.add(applicationPath);
    }

    public void removeApplication(String applicationPath) {
        applications.remove(applicationPath);
    }

    public String getPoeExeLocation() {
        return poeExeLocation;
    }

    public void setPoeExeLocation(String poeExeLocation) {
        this.poeExeLocation = poeExeLocation;
    }

    public PoeVersion getSelectedPoeVersion() {
        return selectedPoeVersion;
    }

    public void setSelectedPoeVersion(PoeVersion selectedPoeVersion) {
        this.selectedPoeVersion = selectedPoeVersion;
    }
}
