package org.TBCreates.TBGeneral.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashSet;
import java.util.UUID;

public class CommandSpyListener implements Listener {
    private final HashSet<UUID> commandSpyEnabled;

    public CommandSpyListener(HashSet<UUID> commandSpyEnabled) {
        this.commandSpyEnabled = commandSpyEnabled;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player sender = event.getPlayer();
        String command = event.getMessage();

        for (UUID uuid : commandSpyEnabled) {
            Player admin = Bukkit.getPlayer(uuid);
            if (admin != null && admin.isOnline() && !admin.equals(sender)) {
                admin.sendMessage(ChatColor.GRAY + "[CommandSpy] " + sender.getName() + " executed: " + command);
            }
        }
    }
}