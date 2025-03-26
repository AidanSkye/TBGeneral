package org.TBCreates.TBGeneral.commands;

import org.TBCreates.TBGeneral.TBGeneral;
import org.TBCreates.TBGeneral.commands.admin.*;
import org.TBCreates.TBGeneral.commands.menu.Menu;
import org.TBCreates.TBGeneral.commands.menu.OpenSelectorMenuCommand;
import org.TBCreates.TBGeneral.commands.player.message.MsgCommand;
import org.TBCreates.TBGeneral.commands.player.message.ReplyCommand;
import org.TBCreates.TBGeneral.commands.player.teleport.TpaCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TBGeneralCommand implements CommandExecutor, TabCompleter {
    private final TBGeneral plugin;

    public TBGeneralCommand(TBGeneral plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check for no arguments
        if (args.length == 0) {
            sender.sendMessage(this.plugin.getPrefix() + " Usage: /tbgeneral reload or /tbg <command>");
            return true;
        }

        // Handle "reload" subcommand
        if (args[0].equalsIgnoreCase("reload")) {
            return handleReload(sender);
        }

        // Forward other subcommands to the appropriate handler
        return handleSubcommand(sender, args);
    }

    private boolean handleReload(CommandSender sender) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("tbgeneral.admin")) {
                player.sendMessage(this.plugin.getPrefix() + " You do not have permission to reload the plugin.");
                return false;
            }
        }

        // Reload the plugin's config and prefix
        this.plugin.reloadConfig();
        this.plugin.loadPrefix();  // Ensure prefix is updated after reloading config

        sender.sendMessage(this.plugin.getPrefix() + " Plugin reloaded successfully!");
        return true;
    }

    private boolean handleSubcommand(CommandSender sender, String[] args) {
        // Add mappings for all subcommands
        switch (args[0].toLowerCase()) {
            case "tpa":
                return new TpaCommand().onCommand(sender, null, "tpa", args);
            case "msg":
                return new MsgCommand(this.plugin).onCommand(sender, null, "msg", args);
            case "reply":
                return new ReplyCommand(this.plugin).onCommand(sender, null, "reply", args);
            case "heal":
                return new GameModeCommand(this.plugin).onCommand(sender, null, "heal", args);
            case "bring":
                return new GameModeCommand(this.plugin).onCommand(sender, null, "bring", args);
            case "goto":
                return new GameModeCommand(this.plugin).onCommand(sender, null, "goto", args);
            case "fly":
                return new fly(this.plugin).onCommand(sender, null, "fly", args);
            case "vanish":
                return new AdminVanishCommand(this.plugin).onCommand(sender, null, "vanish", args);
            case "commandspy":
                return new CommandSpyCommand(new HashSet<>()).onCommand(sender, null, "commandspy", args);
            case "messagespy":
                return new MessageSpyCommand(this.plugin).onCommand(sender, null, "messagespy", args);
            case "gmc":
            case "gms":
            case "gma":
            case "gmsp":
                return new GameModeCommand(this.plugin).onCommand(sender, null, args[0], args);
            case "openselectormenu":
                return new OpenSelectorMenuCommand(this.plugin, new Menu(this.plugin)).onCommand(sender, null, "openselectormenu", args);
            case "givebook":
                return new ForceGiveBookCommand(this.plugin).onCommand(sender, null, "givebook", args);
            case "tbgeneral":
                return new TBGeneralCommand(this.plugin).onCommand(sender, null, "tbgeneral", args);
            default:
                sender.sendMessage(this.plugin.getPrefix() + " Unknown subcommand: /tbg " + args[0]);
                return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 1) {
            // Suggest subcommands like tpa, msg, heal, etc.
            suggestions.add("tpa");
            suggestions.add("msg");
            suggestions.add("reply");
            suggestions.add("heal");
            suggestions.add("bring");
            suggestions.add("goto");
            suggestions.add("fly");
            suggestions.add("vanish");
            suggestions.add("commandspy");
            suggestions.add("messagespy");
            suggestions.add("gmc");
            suggestions.add("gms");
            suggestions.add("gma");
            suggestions.add("gmsp");
            suggestions.add("openselectormenu");
            suggestions.add("givebook");
            suggestions.add("reload");
        }
        return suggestions;
    }
}
