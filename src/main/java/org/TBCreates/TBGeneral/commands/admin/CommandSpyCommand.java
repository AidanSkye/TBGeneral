package org.TBCreates.TBGeneral.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

public class CommandSpyCommand implements CommandExecutor {
    private final HashSet<UUID> commandSpyEnabled;

    public CommandSpyCommand(HashSet<UUID> commandSpyEnabled) {
        this.commandSpyEnabled = commandSpyEnabled;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (commandSpyEnabled.contains(player.getUniqueId())) {
            commandSpyEnabled.remove(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "CommandSpy has been disabled.");
        } else {
            commandSpyEnabled.add(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "CommandSpy has been enabled.");
        }
        return true;
    }
}
