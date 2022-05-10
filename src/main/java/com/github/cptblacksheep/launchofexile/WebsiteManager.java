package com.github.cptblacksheep.launchofexile;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class WebsiteManager {
    private ArrayList<String> websites = new ArrayList<>();

    public static void openWebsite(String website) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI(website));
    }

    public void openAllWebsites() {
        websites.forEach(website -> {
            try {
                openWebsite(website);
            } catch (URISyntaxException | IOException e) {
                JOptionPane.showMessageDialog(
                        null, "Failed to open website: " + website,
                        "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public ArrayList<String> getWebsites() {
        return websites;
    }

    public void setWebsites(ArrayList<String> websites) {
        this.websites = websites;
    }

    public void addWebsite(String website) {
        websites.add(website);
    }

    public void removeWebsite(String website) {
        websites.remove(website);
    }
}
