package org.TBCreates.TBGeneral;

import org.TBCreates.TBGeneral.Listeners.CommandSpyListener;
import org.TBCreates.TBGeneral.commands.*;
import org.TBCreates.TBGeneral.commands.admin.*;
import org.TBCreates.TBGeneral.commands.menu.Menu;
import org.TBCreates.TBGeneral.commands.menu.OpenSelectorMenuCommand;
import org.TBCreates.TBGeneral.commands.player.message.MsgCommand;
import org.TBCreates.TBGeneral.commands.player.message.ReplyCommand;
import org.TBCreates.TBGeneral.commands.player.teleport.TpaCommand;
import org.TBCreates.TBGeneral.commands.player.teleport.TpacceptCommand;
import org.TBCreates.TBGeneral.handlers.PlayerHandler;
import org.TBCreates.TBGeneral.handlers.TorchHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public final class TBGeneral extends JavaPlugin implements Listener {

    // Declare the instance variable for the prefix
    private String prefix;

    private final HashSet<UUID> commandSpyEnabled = new HashSet<>();

    // HashMap to store teleport requests
    private final HashMap<UUID, UUID> teleportRequests = new HashMap<>();

    // Set to track vanished players
    private final Set<UUID> vanishedPlayers = new HashSet<>();

    public static Plugin getInstance() {
        return null;
    }

    @Override
    public void onEnable() {
        // Save the default config if it doesn't exist
        saveDefaultConfig();

        // Load the prefix from the config file before using it
        loadPrefix();

        // Set texture pack URL in server.properties
        updateTexturePackInServerProperties();

        // Example: Logging with the prefix
        Bukkit.getLogger().info("-------------------------------------");
        Bukkit.getLogger().info(prefix + " Plugin enabled!");
        Bukkit.getLogger().info("-------------------------------------");

        // Register commands and listeners
        registerCommands();
        getServer().getPluginManager().registerEvents(this, this);  // Register the event listeners

        // Initialize handlers
        new TorchHandler(this);
        new PlayerHandler(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info(prefix + " Stopping");
    }

    // Getter for the prefix
    public String getPrefix() {
        return prefix;
    }

    // Getter for teleportRequests
    public HashMap<UUID, UUID> getTeleportRequests() {
        return teleportRequests;
    }

    // Load the prefix from the config and apply color codes
    public void loadPrefix() {
        // Load the prefix from the config (with a default value if it's not set)
        this.prefix = getConfig().getString("prefix", "&7[TBGeneral] ");
        // Apply color codes to the prefix
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
    }

    // New Method: Update Texture Pack URL in server.properties
    private void updateTexturePackInServerProperties() {
        // Get the texture pack URL from the config
        String texturePackUrl = getConfig().getString("texture-pack-url");

        if (texturePackUrl == null || texturePackUrl.isEmpty()) {
            getLogger().warning("Texture pack URL not set in config.yml. Skipping update of server.properties.");
            return;
        }

        // Load the server.properties file
        File serverPropertiesFile = new File("server.properties");
        if (!serverPropertiesFile.exists()) {
            getLogger().severe("server.properties file not found! Cannot set the texture pack URL.");
            return;
        }

        try {
            // Load properties from the file
            Properties properties = new Properties();
            try (FileInputStream inputStream = new FileInputStream(serverPropertiesFile)) {
                properties.load(inputStream);
            }

            // Update the texture pack URL
            properties.setProperty("resource-pack", texturePackUrl);

            // Save the updated properties back to the file
            try (FileOutputStream outputStream = new FileOutputStream(serverPropertiesFile)) {
                properties.store(outputStream, "Updated by TBGeneral plugin");
            }

            getLogger().info("Successfully updated texture pack URL in server.properties.");

        } catch (IOException e) {
            getLogger().severe("An error occurred while updating server.properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Register commands
    private void registerCommands() {
        getCommand("givebook").setExecutor(new ForceGiveBookCommand(this));
        getCommand("fly").setExecutor(new fly(this));
        getCommand("menu").setExecutor(new Menu(this)); // Make sure Menu is the correct executor
        getCommand("tbgeneral").setExecutor(new TBGeneralCommand(this));
        getCommand("tbgeneral").setTabCompleter(new TBGeneralCommand(this));

        // Additional Game Mode and Other Commands
        Menu adminMenu = new Menu(this);
        OpenSelectorMenuCommand openSelectorMenuCommand = new OpenSelectorMenuCommand(this, adminMenu);
        getCommand("openselectormenu").setExecutor(openSelectorMenuCommand);

        getServer().getPluginManager().registerEvents(new CommandSpyListener(commandSpyEnabled), this);
        getCommand("commandspy").setExecutor(new CommandSpyCommand(commandSpyEnabled));

        getCommand("gmc").setExecutor(new GameModeCommand(this));
        getCommand("gms").setExecutor(new GameModeCommand(this));
        getCommand("gma").setExecutor(new GameModeCommand(this));
        getCommand("gmsp").setExecutor(new GameModeCommand(this));
        getCommand("heal").setExecutor(new GameModeCommand(this)); // Register heal command
        getCommand("bring").setExecutor(new GameModeCommand(this));
        getCommand("goto").setExecutor(new GameModeCommand(this));

        this.getCommand("msg").setExecutor(new MsgCommand(this));
        this.getCommand("reply").setExecutor(new ReplyCommand(this));
        getCommand("tpa").setExecutor(new TpaCommand());
        getCommand("tpaccept").setExecutor(new TpacceptCommand(this));

        boolean allowTpToSelf = getConfig().getBoolean("settings.allow-tp-to-self", false);
        getLogger().info("Allow teleport to self: " + allowTpToSelf);

        getCommand("vanish").setExecutor(new AdminVanishCommand(this));
        getServer().getPluginManager().registerEvents(this, this);
    }

    // Custom join message
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Check if the player was vanished when they left
        if (vanishedPlayers.contains(playerUUID)) {
            // Hide the player from others and suppress the join message
            event.setJoinMessage(null);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.hidePlayer(this, player);
            }
            return;
        }

        // Set custom join message for non-vanished players
        event.setJoinMessage(getPrefix() + " Welcome " + player.getName() + " to the server!");
    }

    // Custom leave message
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Check if the player is vanished and persist their vanished state
        if (AdminVanishCommand.vanished.contains(player)) {
            vanishedPlayers.add(playerUUID);
            event.setQuitMessage(null); // Suppress leave message
        } else {
            vanishedPlayers.remove(playerUUID); // Remove from vanished list if no longer vanished
        }

        // Set custom quit message for non-vanished players
        if (!vanishedPlayers.contains(playerUUID)) {
            event.setQuitMessage(getPrefix() + " Goodbye " + player.getName() + "!");
        }
    }
}
