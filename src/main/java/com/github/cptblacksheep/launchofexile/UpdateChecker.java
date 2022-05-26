package com.github.cptblacksheep.launchofexile;

import com.github.cptblacksheep.launchofexile.datamanagement.WebsiteManager;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class UpdateChecker {
    public static final String VERSION = "1.1.3";

    private UpdateChecker() {
    }

    public static void checkForNewVersion() {
        String latestVersionTag = latestVersionTag();
        boolean newVersionAvailable = newVersionAvailable(latestVersionTag);

        if (newVersionAvailable)
            showNewVersionDialog();
    }

    private static String latestVersionTag() {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest
                .newBuilder(URI.create("https://github.com/CptBlacksheep/Launch-of-Exile/releases/latest"))
                .timeout(Duration.ofSeconds(5))
                .build();

        try {
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            String path = response.uri().getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (IOException | InterruptedException e) {
            return "";
        }
    }

    private static boolean newVersionAvailable(String latestVersionTag) {

        if (latestVersionTag == null || latestVersionTag.isBlank())
            return false;

        return !latestVersionTag.equals("v" + VERSION);
    }

    private static void showNewVersionDialog() {
        Object[] choices = {"Go to the release page", "Skip"};
        int option = JOptionPane.showOptionDialog(null,
                "A new LoE version is available.",
                "Launch of Exile - Update",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                choices, null);

        if (option != JOptionPane.OK_OPTION)
            return;

        try {
            WebsiteManager.openWebsite("https://github.com/CptBlacksheep/Launch-of-Exile/releases/latest");
        } catch (URISyntaxException | IOException e) {
            //Ignore
        }

        System.exit(0);
    }
}
