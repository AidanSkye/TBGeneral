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
import org.TBCreates.TBGeneral.commands.player.teleport.TpdenyCommand;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public final class TBGeneral extends JavaPlugin implements Listener {

    // Declare the instance variable for the prefix
    private String prefix;

    // HashMap to store teleport requests and vanished players
    private final HashMap<UUID, UUID> teleportRequests = new HashMap<>();
    private final Set<UUID> vanishedPlayers = new HashSet<>();

    private final HashSet<UUID> commandSpyEnabled = new HashSet<>(); // For command spy

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
        this.prefix = getConfig().getString("prefix", "&7[TBGeneral] ");
        this.prefix = ChatColor.translateAlternateColorCodes('&', prefix);
    }

    // New Method: Update Texture Pack URL in server.properties
    private void updateTexturePackInServerProperties() {
        String texturePackUrl = getConfig().getString("texture-pack-url");

        if (texturePackUrl == null || texturePackUrl.isEmpty()) {
            getLogger().warning("Texture pack URL not set in config.yml. Skipping update of server.properties.");
            return;
        }

        File serverPropertiesFile = new File("server.properties");
        if (!serverPropertiesFile.exists()) {
            getLogger().severe("server.properties file not found! Cannot set the texture pack URL.");
            return;
        }

        try {
            Properties properties = new Properties();
            try (FileInputStream inputStream = new FileInputStream(serverPropertiesFile)) {
                properties.load(inputStream);
            }

            properties.setProperty("resource-pack", texturePackUrl);

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
        // Register the main /tbg command
        getCommand("tbgeneral").setExecutor(new TBGeneralCommand(this));
        getCommand("tbgeneral").setTabCompleter(new TBGeneralCommand(this));

        // Register other commands directly (as usual)
        getCommand("fly").setExecutor(new fly(this));
        getCommand("givebook").setExecutor(new ForceGiveBookCommand(this));
        getCommand("heal").setExecutor(new GameModeCommand(this));
        getCommand("tpa").setExecutor(new TpaCommand());
        getCommand("msg").setExecutor(new MsgCommand(this));
        getCommand("tbg").setExecutor(new TBGeneralCommand(this)); // This also makes /tbg work
        getCommand("gmc").setExecutor(new GameModeCommand(this));
        getCommand("gms").setExecutor(new GameModeCommand(this));
        getCommand("gmsp").setExecutor(new GameModeCommand(this));
        getCommand("gma").setExecutor(new GameModeCommand(this));
        getCommand("messagespy").setExecutor(new MessageSpyCommand(this));
        getCommand("tpaccept").setExecutor(new TpacceptCommand(this));
        getCommand("tpdeny").setExecutor(new TpdenyCommand());
        getCommand("menu").setExecutor(new Menu(this));
        this.getCommand("msg").setExecutor(new MsgCommand(this));
        this.getCommand("reply").setExecutor(new ReplyCommand(this));

        boolean allowTpToSelf = getConfig().getBoolean("settings.allow-tp-to-self", false);
        getLogger().info("Allow teleport to self: " + allowTpToSelf);

        getCommand("vanish").setExecutor(new AdminVanishCommand(this));
        getServer().getPluginManager().registerEvents(this, this);

        getServer().getPluginManager().registerEvents(new CommandSpyListener(commandSpyEnabled), this);
        getCommand("commandspy").setExecutor(new CommandSpyCommand(commandSpyEnabled));

        Menu adminMenu = new Menu(this);
        OpenSelectorMenuCommand openSelectorMenuCommand = new OpenSelectorMenuCommand(this, adminMenu);
        getCommand("openselectormenu").setExecutor(openSelectorMenuCommand);
    }

    // Custom join message
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (vanishedPlayers.contains(playerUUID)) {
            event.setJoinMessage(null);
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.hidePlayer(this, player);
            }
        } else {
            event.setJoinMessage(getPrefix() + " Welcome " + player.getName() + " to the server!");
        }
    }

    // Custom leave message
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (vanishedPlayers.contains(playerUUID)) {
            event.setQuitMessage(null); // Suppress leave message
        } else {
            event.setQuitMessage(getPrefix() + " Goodbye " + player.getName() + "!");
        }
    }
}
