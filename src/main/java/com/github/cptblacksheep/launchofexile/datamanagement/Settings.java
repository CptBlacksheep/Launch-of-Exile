package com.github.cptblacksheep.launchofexile.datamanagement;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Objects;

public class Settings {
    private static Settings INSTANCE;
    private PoeVersion selectedPoeVersion;
    private String poeExeLocation;
    private boolean darkModeEnabled;
    private boolean ahkSupportEnabled;
    private String ahkExeLocation;

    private Settings() {
        poeExeLocation = "";
        ahkExeLocation = "";
        selectedPoeVersion = PoeVersion.STEAM;
        darkModeEnabled = false;
        ahkSupportEnabled = false;
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

    public String getAhkExeLocation() {
        return ahkExeLocation;
    }

    public void setAhkExeLocation(String ahkExeLocation) {
        this.ahkExeLocation = Objects.requireNonNull(ahkExeLocation);
    }

    public boolean isAhkSupportEnabled() {
        return ahkSupportEnabled;
    }

    public void setAhkSupportEnabled(boolean ahkSupportEnabled) {
        this.ahkSupportEnabled = ahkSupportEnabled;
    }
}
