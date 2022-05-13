package com.github.cptblacksheep.launchofexile.datamanagement;

import java.util.Objects;

public class Settings {
    private String poeExeLocation = "";
    private PoeVersion selectedPoeVersion = PoeVersion.STEAM;
    private boolean darkModeEnabled;

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

    public boolean getDarkModeEnabled() { return darkModeEnabled; }

    public void setDarkModeEnabled(boolean value) { this.darkModeEnabled = value; }
}
