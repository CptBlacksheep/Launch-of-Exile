package com.github.cptblacksheep.launchofexile.datamanagement;

public enum PoeVersion {
    STEAM("Steam"),
    STANDALONE("Standalone");

    private final String displayName;

    PoeVersion(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
