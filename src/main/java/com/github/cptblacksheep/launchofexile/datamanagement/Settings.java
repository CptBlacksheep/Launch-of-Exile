package com.github.cptblacksheep.launchofexile.datamanagement;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

public class Settings {
    private static Settings INSTANCE;
    private String poeExeLocation;
    private PoeVersion selectedPoeVersion;
    private boolean darkModeEnabled;

    private Settings() {
        poeExeLocation = "";
        selectedPoeVersion = PoeVersion.STEAM;
        darkModeEnabled = false;
    }

    @JsonCreator
    public static Settings getSettings() {
        if (INSTANCE == null)
            INSTANCE = new Settings();

        return INSTANCE;
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

    public boolean isDarkModeEnabled() {
        return darkModeEnabled;
    }

    public void setDarkModeEnabled(boolean darkModeEnabled) {
        this.darkModeEnabled = darkModeEnabled;
    }
}
