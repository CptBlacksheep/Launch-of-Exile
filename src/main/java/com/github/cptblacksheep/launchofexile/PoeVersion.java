package com.github.cptblacksheep.launchofexile;

public enum PoeVersion {
    STEAM("Steam"),
    STANDALONE("Standalone");

    private final String displayName;

    PoeVersion(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
