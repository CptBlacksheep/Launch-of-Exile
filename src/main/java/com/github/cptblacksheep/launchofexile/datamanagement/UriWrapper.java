package com.github.cptblacksheep.launchofexile.datamanagement;

import java.util.Objects;

public class UriWrapper {
    private String uri;
    private String name;
    private boolean enabled = true;

    UriWrapper() { //default constructor for jackson to work
        this("", "");
    }

    public UriWrapper(String uri) {
        this(uri, uri);
    }

    public UriWrapper(String uri, String name) {
        this.uri = Objects.requireNonNull(uri);
        this.name = Objects.requireNonNull(name);
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

}
