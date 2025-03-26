package org.TBCreates.TBGeneral.commands.message;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ReplyCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public ReplyCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player senderPlayer = (Player) sender;

        if (args.length < 1) {
            senderPlayer.sendMessage(getPrefix() + ChatColor.RED + "Usage: /reply <message>");
            return true;
        }

        Player targetPlayer = MsgCommand.lastMessaged.get(senderPlayer);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            senderPlayer.sendMessage(getPrefix() + ChatColor.RED + "No one to reply to or the player is offline.");
            return true;
        }

        String message = String.join(" ", args);

        // Send the messages with the prefix
        targetPlayer.sendMessage(ChatColor.GOLD + "[From " + senderPlayer.getName() + "]: " + ChatColor.WHITE + message);
        senderPlayer.sendMessage( ChatColor.GOLD + "[To " + targetPlayer.getName() + "]: " + ChatColor.WHITE + message);

        // Play a ding sound for the receiver
        playDing(targetPlayer);

        // Update the last messaged map to keep the conversation flowing
        MsgCommand.lastMessaged.put(senderPlayer, targetPlayer);
        MsgCommand.lastMessaged.put(targetPlayer, senderPlayer);

        return true;
    }

    private void playDing(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
    }

    private String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix", "&6[MyPlugin] &r"));
    }
}
