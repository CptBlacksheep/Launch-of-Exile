package com.github.cptblacksheep.launchofexile.datamanagement;

import java.util.Objects;

public class UriWrapper {
    private String uri;
    private String name;
    private boolean enabled;

    public UriWrapper() {
        this("", "", true);
    }

    public UriWrapper(String uri) {
        this(uri, "", true);
    }

    public UriWrapper(String uri, String name) {
        this(uri, name, true);
    }

    public UriWrapper(String uri, String name, boolean enabled) {
        this.uri = Objects.requireNonNull(uri);
        this.name = Objects.requireNonNull(name);
        this.enabled = enabled;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = Objects.requireNonNull(uri);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return uri;
    }
}
