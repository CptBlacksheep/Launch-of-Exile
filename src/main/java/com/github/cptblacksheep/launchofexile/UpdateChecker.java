package com.github.cptblacksheep.launchofexile;

import com.github.cptblacksheep.launchofexile.datamanagement.Settings;
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
    public static final String VERSION = "1.2.0";

    private UpdateChecker() {
    }

    public static void startupCheckForNewVersion() {
        boolean updateNotificationsEnabled = Settings.getSettings().isUpdateNotificationsEnabled();

        if (!updateNotificationsEnabled)
            return;

        boolean newVersionAvailable = checkForNewVersion();

        if (newVersionAvailable)
            showNewVersionDialog(true);
    }

    public static boolean checkForNewVersion() {
        String latestVersionTag = latestVersionTag();

        if (latestVersionTag == null)
            return false;

        return newVersionAvailable(latestVersionTag);
    }

    public static void showNewVersionDialog(boolean exitAfterGoToReleasePage) {
        Object[] choices = {"Go to the release page", "Close dialog"};
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
            JOptionPane.showMessageDialog(
                    null, "Failed to open release page.",
                    "Launch of Exile - Error", JOptionPane.ERROR_MESSAGE);
        }

        if (exitAfterGoToReleasePage)
            System.exit(0);
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
            return null;
        }
    }

    private static boolean newVersionAvailable(String latestVersionTag) {
        return !latestVersionTag.equals("v" + VERSION);
    }
}
