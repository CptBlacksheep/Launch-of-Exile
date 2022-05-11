package com.github.cptblacksheep.launchofexile.datamanagement;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

public class WebsiteManager {
    private ArrayList<UriWrapper> websites = new ArrayList<>();

    public static void openWebsite(String websiteUri) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(websiteUri));
    }

    public static void openWebsite(UriWrapper website) throws URISyntaxException, IOException {
        openWebsite(website.getUri());
    }

    public void openAllEnabledWebsites() {
        websites.stream().filter(UriWrapper::isEnabled).forEach(website -> {
            try {
                openWebsite(website);
            } catch (URISyntaxException | IOException e) {
                JOptionPane.showMessageDialog(
                        null, "Failed to open website: " + website,
                        "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public ArrayList<UriWrapper> getWebsites() {
        return websites;
    }

    public void setWebsites(ArrayList<UriWrapper> websites) {
        this.websites = Objects.requireNonNull(websites);
    }

    public void addWebsite(UriWrapper website) {
        websites.add(Objects.requireNonNull(website));
    }

    public void removeWebsite(UriWrapper website) {
        websites.remove(website);
    }
}
