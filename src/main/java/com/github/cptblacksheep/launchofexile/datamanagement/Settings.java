package com.github.cptblacksheep.launchofexile.datamanagement;

import java.util.Objects;

public class Settings {
    private String poeExeLocation = "";
    private PoeVersion selectedPoeVersion = PoeVersion.STEAM;

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
