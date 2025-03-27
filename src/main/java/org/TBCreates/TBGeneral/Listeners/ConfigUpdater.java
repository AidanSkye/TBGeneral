package org.TBCreates.TBGeneral.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Set;

public class ConfigUpdater {
    private static String prefix = "&7[&bTBGeneral&7] "; // Default prefix

    public static void updateConfig(JavaPlugin plugin) {
        loadPrefix(plugin); // Load prefix from config.yml

        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            plugin.saveDefaultConfig(); // Save default config if it doesn’t exist
            plugin.getLogger().info(format("&aDefault config.yml created!"));
            return;
        }

        FileConfiguration currentConfig = YamlConfiguration.loadConfiguration(configFile); // Load existing config
        FileConfiguration defaultConfig = getDefaultConfig(plugin); // Load defaults from JAR

        if (defaultConfig == null) {
            plugin.getLogger().warning(format("&cFailed to load default config.yml!"));
            return;
        }

        boolean updated = false;

        // Merge missing keys from defaultConfig into currentConfig
        for (String key : defaultConfig.getKeys(true)) {
            if (!currentConfig.contains(key)) {
                currentConfig.set(key, defaultConfig.get(key));
                updated = true;
            }
        }

        if (updated) {
            try {
                currentConfig.save(configFile);
                plugin.getLogger().info(format("&aConfig updated with new settings!"));
            } catch (IOException e) {
                plugin.getLogger().warning(format("&cFailed to update config: " + e.getMessage()));
            }
        }
    }

    private static FileConfiguration getDefaultConfig(JavaPlugin plugin) {
        InputStream defaultConfigStream = plugin.getResource("config.yml");
        if (defaultConfigStream == null) return null;

        return YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
    }

    private static void loadPrefix(JavaPlugin plugin) {
        prefix = plugin.getConfig().getString("Prefix", "&7[&bTBGeneral&7] ");
        prefix = ChatColor.translateAlternateColorCodes('&', prefix); // Convert color codes
    }

    private static String format(String message) {
        return prefix + ChatColor.translateAlternateColorCodes('&', message);
    }
}
