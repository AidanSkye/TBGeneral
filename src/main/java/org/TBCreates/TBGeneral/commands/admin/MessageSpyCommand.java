package org.TBCreates.TBGeneral.commands.admin;

import org.TBCreates.TBGeneral.TBGeneral;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class MessageSpyCommand implements CommandExecutor {

    public static final Set<Player> spyingAdmins = new HashSet<>();
    private final TBGeneral plugin;

    // Constructor that takes TBGeneral plugin as an argument
    public MessageSpyCommand(TBGeneral plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("tbgeneral.messagespy")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (spyingAdmins.contains(player)) {
            spyingAdmins.remove(player);
            player.sendMessage(ChatColor.YELLOW + "MessageSpy has been " + ChatColor.RED + "disabled.");
            System.out.println(player.getName() + " has disabled MessageSpy.");
        } else {
            spyingAdmins.add(player);
            player.sendMessage(ChatColor.YELLOW + "MessageSpy has been " + ChatColor.GREEN + "enabled.");
            System.out.println(player.getName() + " has enabled MessageSpy.");
        }

        return true;
    }
}
