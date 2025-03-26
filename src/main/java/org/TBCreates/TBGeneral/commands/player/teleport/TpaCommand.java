package org.TBCreates.TBGeneral.commands.player.teleport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class TpaCommand implements CommandExecutor {

    public static final HashMap<UUID, UUID> teleportRequests = new HashMap<>();
    public static final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private static final int COOLDOWN_TIME = 30; // 30 seconds cooldown

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player requester = (Player) sender;
        UUID requesterUUID = requester.getUniqueId();

        // Check cooldown
        if (cooldowns.containsKey(requesterUUID)) {
            long secondsLeft = ((cooldowns.get(requesterUUID) / 1000) + COOLDOWN_TIME) - (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                requester.sendMessage(ChatColor.RED + "You must wait " + secondsLeft + " seconds before using /tpa again.");
                return true;
            }
        }

        if (args.length != 1) {
            requester.sendMessage(ChatColor.RED + "Usage: /tpa <player>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null || !target.isOnline()) {
            requester.sendMessage(ChatColor.RED + "Player not found or is offline.");
            return true;
        }

        if (target == requester) {
            requester.sendMessage(ChatColor.RED + "You cannot teleport to yourself.");
            return true;
        }

        // Store teleport request
        teleportRequests.put(target.getUniqueId(), requester.getUniqueId());
        cooldowns.put(requesterUUID, System.currentTimeMillis());

        // Notify both players
        requester.sendMessage(ChatColor.GREEN + "Teleport request sent to " + ChatColor.YELLOW + target.getName() + ".");
        target.sendMessage(ChatColor.YELLOW + requester.getName() + ChatColor.GOLD + " wants to teleport to you. Type " +
                ChatColor.GREEN + "/tpaccept" + ChatColor.GOLD + " to accept or " + ChatColor.RED + "/tpdeny" + ChatColor.GOLD + " to deny.");

        return true;
    }
}
