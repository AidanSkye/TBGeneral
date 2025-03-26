package org.TBCreates.TBGeneral.commands.player.teleport;

import org.TBCreates.TBGeneral.TBGeneral;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TpacceptCommand implements CommandExecutor {

    private final TBGeneral plugin;

    public TpacceptCommand(TBGeneral plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player target = (Player) sender;
        FileConfiguration config = plugin.getConfig();

        // Read configurable settings from config.yml
        int teleportDelay = config.getInt("tpa.teleport-delay", 5);
        boolean enableSounds = config.getBoolean("tpa.enable-sounds", true);
        boolean enableParticles = config.getBoolean("tpa.enable-particles", true);

        if (!TpaCommand.teleportRequests.containsKey(target.getUniqueId())) {
            target.sendMessage(ChatColor.RED + "You have no pending teleport requests.");
            return true;
        }

        UUID requesterUUID = TpaCommand.teleportRequests.get(target.getUniqueId());
        Player requester = Bukkit.getPlayer(requesterUUID);

        if (requester == null || !requester.isOnline()) {
            target.sendMessage(ChatColor.RED + "The player who requested to teleport is offline.");
            TpaCommand.teleportRequests.remove(target.getUniqueId());
            return true;
        }

        target.sendMessage(ChatColor.GREEN + "Teleport request accepted! " + ChatColor.YELLOW + requester.getName() +
                ChatColor.GOLD + " will be teleported in " + teleportDelay + " seconds if they do not move.");
        requester.sendMessage(ChatColor.YELLOW + "Teleporting in " + teleportDelay + " seconds. Do not move!");

        Location initialLocation = requester.getLocation();

        new BukkitRunnable() {
            private int countdown = teleportDelay;

            @Override
            public void run() {
                if (!requester.isOnline()) {
                    cancel();
                    return;
                }

                // Check if the player moved
                if (!requester.getLocation().getBlock().equals(initialLocation.getBlock())) {
                    requester.sendMessage(ChatColor.RED + "Teleport canceled because you moved!");
                    target.sendMessage(ChatColor.RED + requester.getName() + " moved, so the teleport was canceled.");

                    // Play cancel sound & particles
                    if (enableParticles) {
                        requester.getWorld().spawnParticle(Particle.SMOKE, requester.getLocation(), 20);
                    }
                    if (enableSounds) {
                        requester.getWorld().playSound(requester.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.5F);
                    }

                    cancel();
                    return;
                }

                if (countdown > 0) {
                    requester.sendMessage(ChatColor.YELLOW + "Teleporting in " + ChatColor.GOLD + countdown + ChatColor.YELLOW + " seconds...");

                    // Play countdown sound
                    if (enableSounds) {
                        requester.playSound(requester.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.5F);
                    }

                    // Spawn particles around the player
                    if (enableParticles) {
                        requester.getWorld().spawnParticle(Particle.PORTAL, requester.getLocation(), 50, 0.5, 1, 0.5);
                    }

                    countdown--;
                } else {
                    // Teleport and play effects
                    requester.teleport(target.getLocation());
                    requester.sendMessage(ChatColor.GREEN + "You have teleported to " + ChatColor.YELLOW + target.getName() + "!");
                    target.sendMessage(ChatColor.YELLOW + requester.getName() + ChatColor.GREEN + " has teleported to you!");

                    // Play teleport sound & particles
                    if (enableParticles) {
                        requester.getWorld().spawnParticle(Particle.END_ROD, requester.getLocation(), 40, 0.5, 1, 0.5);
                    }
                    if (enableSounds) {
                        requester.getWorld().playSound(requester.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                    }

                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Runs every second

        TpaCommand.teleportRequests.remove(target.getUniqueId());
        return true;
    }
}
