package org.TBCreates.TBGeneral.commands.player.message;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class MsgCommand implements CommandExecutor {

    private final JavaPlugin plugin;
    public static final Map<Player, Player> lastMessaged = new HashMap<>();

    public MsgCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player senderPlayer = (Player) sender;

        if (args.length < 2) {
            senderPlayer.sendMessage(getPrefix() + ChatColor.RED + "Usage: /msg <player> <message>");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayerExact(args[0]);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            senderPlayer.sendMessage(getPrefix() + ChatColor.RED + "Player not found or is offline.");
            return true;
        }

        String message = String.join(" ", args).substring(args[0].length()).trim();

        // Send the messages with the prefix
        targetPlayer.sendMessage(ChatColor.GOLD + "[From " + senderPlayer.getName() + "]: " + ChatColor.WHITE + message);
        senderPlayer.sendMessage(ChatColor.GOLD + "[To " + targetPlayer.getName() + "]: " + ChatColor.WHITE + message);

        // Play a ding sound for the receiver
        playDing(targetPlayer);

        // Update the last messaged map
        lastMessaged.put(senderPlayer, targetPlayer);
        lastMessaged.put(targetPlayer, senderPlayer);

        return true;
    }

    private void playDing(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
    }

    private String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix", "&6[MyPlugin] &r"));
    }
}
