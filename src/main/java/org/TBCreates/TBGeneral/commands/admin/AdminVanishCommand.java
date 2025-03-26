package org.TBCreates.TBGeneral.commands.admin;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class AdminVanishCommand implements CommandExecutor, Listener {

    private final JavaPlugin plugin;
    private final LuckPerms luckPerms;
    public static final Set<Player> vanished = new HashSet<>();

    public AdminVanishCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.luckPerms = LuckPermsProvider.get();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;
        FileConfiguration config = plugin.getConfig();

        String vanishMessage = ChatColor.translateAlternateColorCodes('&',
                config.getString("vanish.messages.vanish", "&7You have vanished."));
        String unvanishMessage = ChatColor.translateAlternateColorCodes('&',
                config.getString("vanish.messages.unvanish", "&aYou are no longer vanished."));

        if (vanished.contains(player)) {
            unvanishPlayer(player);
            player.sendMessage(unvanishMessage);
        } else {
            vanishPlayer(player);
            player.sendMessage(vanishMessage);
        }

        return true;
    }

    private void vanishPlayer(Player player) {
        vanished.add(player);

        // Hide from all players except admins with vanish permission
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("tbgeneral.vanish.see")) {
                p.hidePlayer(plugin, player);
            }
        }

        updateTabList();
        makeInvisibleToMobs(player);

        // Apply self-visibility
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
    }

    private void unvanishPlayer(Player player) {
        vanished.remove(player);

        // Show player to everyone again
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(plugin, player);
        }

        updateTabList();
        restoreMobTargeting(player);

        // Remove self-visibility effects
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.removePotionEffect(PotionEffectType.GLOWING);
    }

    private void updateTabList() {
        FileConfiguration config = plugin.getConfig();
        String vanishPrefix = ChatColor.translateAlternateColorCodes('&',
                config.getString("vanish.tablist.prefix", "&c[V] "));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (vanished.contains(p)) {
                p.setPlayerListName(vanishPrefix + ChatColor.WHITE + p.getName());
            } else {
                p.setPlayerListName(ChatColor.WHITE + p.getName());
            }
        }
    }

    private void makeInvisibleToMobs(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, Integer.MAX_VALUE, 1, false, false));

        for (Entity entity : player.getWorld().getEntities()) {
            if (entity instanceof Monster) {
                ((Monster) entity).setTarget(null);
            }
        }
    }

    private void restoreMobTargeting(Player player) {
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updateTabList();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        vanished.remove(event.getPlayer());
        updateTabList();
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();

        if (vanished.contains(player) && event.getInventory().getType() == InventoryType.CHEST) {
            Block chestBlock = event.getInventory().getLocation().getBlock();

            // **Prevent animation and sound**
            preventChestAnimation(chestBlock);
            event.setCancelled(false); // Allow inventory to open
        }
    }

    private void preventChestAnimation(Block chestBlock) {
        if (chestBlock.getState() instanceof Chest) {
            Chest chest = (Chest) chestBlock.getState();
            chest.getWorld().playSound(chest.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 0, 0); // Silent sound
            chest.getWorld().spawnParticle(Particle.SMOKE, chest.getLocation().add(0.5, 1, 0.5), 5, 0, 0, 0, 0);
        }
    }
}
