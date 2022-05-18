package com.github.cptblacksheep.launchofexile.datamanagement;

import com.github.cptblacksheep.launchofexile.exceptions.ExeNotFoundException;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class ApplicationManager {
    private static final String POE_STEAM_ID = "238960";
    private ArrayList<UriWrapper> applications = new ArrayList<>();

    public static void startApplication(String applicationUri) throws IOException {

        if (Files.notExists(Path.of(applicationUri)))
            throw new FileNotFoundException("Application not found");

        new ProcessBuilder(applicationUri).start();
    }

    public static void startApplication(UriWrapper application) throws IOException {
        startApplication(application.getUri());
    }

    public static void startAhkApplication(UriWrapper application) throws IOException {
        Settings settings = Settings.getSettings();
        String ahkExeLocation = settings.getAhkExeLocation();
        String applicationUri = application.getUri();

        if (ahkExeLocation.isBlank() || Files.notExists(Path.of(ahkExeLocation)))
            throw new ExeNotFoundException("AHK .exe not found");

        if (Files.notExists(Path.of(applicationUri)))
            throw new FileNotFoundException("Application not found");

        new ProcessBuilder(ahkExeLocation, applicationUri).start();
    }

    public static void startSteamGameById(String id) throws IOException {
        try {
            WebsiteManager.openWebsite("steam://run/" + id); //Uses Steam browser protocol
        } catch (URISyntaxException ex) {
            throw new IOException(ex); //Won't happen
        }
    }

    public void startAllEnabledApplications() {
        applications.stream().filter(UriWrapper::isEnabled).forEach(application -> {
            try {

                if (application.getUri().toLowerCase().endsWith(".ahk"))
                    startAhkApplication(application);
                else
                    startApplication(application);

            } catch (ExeNotFoundException ex) {
                JOptionPane.showMessageDialog(
                        null, String.format("Failed to launch application: %s%n%n"
                                + "AHK .exe not found.", application.getUri()),
                        "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(
                        null, String.format("Failed to launch application: %s%n%n"
                                + "Application not found.", application.getUri()),
                        "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        null, String.format("Failed to launch application: %s%n%n"
                                + "It's possible that the application needs admin rights (set the launcher to start "
                                + "in admin mode, visit the FAQ at https://github.com/CptBlacksheep/Launch-of-Exile "
                                + "for more information).", application.getUri()),
                        "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void startPoe(PoeVersion poeVersion) {
        try {
            switch (poeVersion) {
                case STEAM -> startSteamGameById(POE_STEAM_ID);
                case STANDALONE -> startApplication(Settings.getSettings().getPoeExeLocation());
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(
                    null, """
                            Failed to launch Path of Exile

                            PoE .exe not found.""",
                    "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null, """
                            Failed to launch Path of Exile

                            It's possible that the wrong PoE version is selected.""",
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

}
