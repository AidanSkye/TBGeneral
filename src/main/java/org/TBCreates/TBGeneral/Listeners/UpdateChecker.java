package org.TBCreates.TBGeneral.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private final Plugin plugin;
    private final String versionFileUrl = "https://raw.githubusercontent.com/Thebestharrison1221/TBGeneral/DEV/version.txt"; // GitHub URL
    private String prefix = "&7[&bTBGeneral&7] "; // Default prefix

    public UpdateChecker(Plugin plugin) {
        this.plugin = plugin;
        loadPrefix(); // Load prefix from config.yml
    }

    // Run onEnable to check for updates
    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String latestVersion = fetchLatestVersion();
                if (latestVersion == null) {
                    Bukkit.getLogger().warning(format("&cCould not fetch the latest version from GitHub."));
                    return;
                }

                String currentVersion = plugin.getConfig().getString("Version", "Unknown");
                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    Bukkit.getLogger().warning(format("&eA new update is available! Current: " + currentVersion + ", Latest: " + latestVersion));
                    Bukkit.getLogger().warning(format("&eDownload it from: https://github.com/Thebestharrison1221/TBGeneral/releases/latest"));
                } else {
                    Bukkit.getLogger().info(format("&aYour plugin is up to date."));
                }
            } catch (Exception e) {
                Bukkit.getLogger().warning(format("&cFailed to check for updates: " + e.getMessage()));
            }
        });
    }

    // Check updates for a player with permission
    public void checkForUpdatesForPlayer(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String latestVersion = fetchLatestVersion();
                if (latestVersion == null) {
                    player.sendMessage(format("&cCould not fetch the latest version from GitHub."));
                    return;
                }

                String currentVersion = plugin.getConfig().getString("Version", "Unknown");
                if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                    player.sendMessage(format("&eA new update is available! Current: " + currentVersion + ", Latest: " + latestVersion));
                    player.sendMessage(format("&eDownload it from: https://github.com/Thebestharrison1221/TBGeneral/releases/latest"));
                } else {
                    player.sendMessage(format("&aYour plugin is up to date."));
                }
            } catch (Exception e) {
                player.sendMessage(format("&cFailed to check for updates: " + e.getMessage()));
            }
        });
    }

    private String fetchLatestVersion() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(versionFileUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String latestVersion = reader.readLine().trim();
            reader.close();

            return latestVersion;
        } catch (Exception e) {
            return null;
        }
    }

    private void loadPrefix() {
        prefix = plugin.getConfig().getString("Prefix", "&7[&bTBGeneral&7] ");
        prefix = ChatColor.translateAlternateColorCodes('&', prefix); // Convert color codes
    }

    private String format(String message) {
        return prefix + ChatColor.translateAlternateColorCodes('&', message);
    }
}
