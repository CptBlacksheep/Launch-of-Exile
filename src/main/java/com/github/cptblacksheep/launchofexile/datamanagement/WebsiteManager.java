package com.github.cptblacksheep.launchofexile.datamanagement;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
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
                        null, String.format("Failed to open website: %s%n%n "
                                        + "Possible cause:%n"
                                        + "- Websites that don't follow the www.xxx.xx scheme are "
                                        + "currently not supported"
                                , website.getUri()),
                        "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public void sortWebsites() {
        websites.sort(Comparator.comparing((UriWrapper uriWrapper) -> !uriWrapper.isEnabled())
                .thenComparing(UriWrapper::getName));
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
