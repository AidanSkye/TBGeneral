package org.TBCreates.TBGeneral.commands.player.teleport;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpdenyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player target = (Player) sender;

        if (!TpaCommand.teleportRequests.containsKey(target.getUniqueId())) {
            target.sendMessage(ChatColor.RED + "You have no pending teleport requests.");
            return true;
        }

        UUID requesterUUID = TpaCommand.teleportRequests.get(target.getUniqueId());
        Player requester = target.getServer().getPlayer(requesterUUID);

        target.sendMessage(ChatColor.RED + "Teleport request denied.");
        if (requester != null && requester.isOnline()) {
            requester.sendMessage(ChatColor.RED + target.getName() + " has denied your teleport request.");
        }

        TpaCommand.teleportRequests.remove(target.getUniqueId());
        return true;
    }
}
