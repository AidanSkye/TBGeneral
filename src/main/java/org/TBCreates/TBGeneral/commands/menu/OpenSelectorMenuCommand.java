package org.TBCreates.TBGeneral.commands.menu;

import org.TBCreates.TBGeneral.TBGeneral;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class OpenSelectorMenuCommand implements Listener, CommandExecutor {

    private final String invName = ChatColor.WHITE + "Game mode Selector";
    private final HashMap<UUID, Inventory> customInventories = new HashMap<>();

    private final Menu menu; // Reference to the Menu class (Admin Menu)

    public OpenSelectorMenuCommand(TBGeneral plugin, Menu adminMenu) {
        // Register this class to listen for inventory click events
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.menu = adminMenu; // Store reference to the Menu (Admin Menu) class
    }
    // Handle the command to open the menu
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command.");
            return true;
        }

        Player player = (Player) sender;
        openSelectorMenu(player);
        return true;
    }

    // Handle inventory clicks
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Ensure this is a custom inventory by checking the map
        if (!customInventories.containsValue(event.getClickedInventory())) {
            return;
        }

        // Cancel the event to prevent default interactions
        event.setCancelled(true);

        // Ensure the clicked slot is valid
        if (event.getSlot() < 0) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        // Handle specific slot actions
        switch (slot) {
            // Pink Glass - Execute /gmsel command
            case 34:
            case 33:
            case 32:
            case 41:
            case 42:
            case 43:
            case 52:
            case 51:
            case 50:
                player.chat("/menu");
                break;

            case 1:
            case 2:
            case 3:
            case 10:
            case 11:
            case 12:
            case 19:
            case 20:
            case 21:
                break;

            default:
                break; // No action for other slots
        }
    }

    // Open the Selector Menu
    private void openSelectorMenu(Player player) {
        // Create the inventory
        Inventory inv = Bukkit.createInventory(player, 54, invName);

        // Green Glass (Right section)
        int[] greenGlassSlots = {5, 6, 7, 14, 15, 16, 23, 24, 25};
        for (int slot : greenGlassSlots) {
            inv.setItem(slot, getGlassPaneItem(Material.AIR, "&4COMING SOON", "&aCome back later"));
        }

        // Blue Glass (Center-left section)
        int[] blueGlassSlots = {1, 2, 3, 10, 11, 12, 19, 20, 21};
        for (int slot : blueGlassSlots) {
            inv.setItem(slot, getGlassPaneItem(Material.AIR, "&4COMING SOON", "&aCome back later"));
        }

        // Pink Glass (Bottom-right section)
        int[] pinkGlassSlots = {34, 33, 32, 41, 42, 43, 52, 51, 50};
        for (int slot : pinkGlassSlots) {
            inv.setItem(slot, getGlassPaneItem(Material.AIR, "&dGamemode Select", "&aClick to open Admin menu"));
        }

        // Gray Glass (Bottom-left section)
        int[] grayGlassSlots = {28, 29, 30, 37, 38, 39, 46, 47, 48};
        for (int slot : grayGlassSlots) {
            inv.setItem(slot, getGlassPaneItem(Material.AIR, "&4COMING SOON", "&aCome back later"));
        }

        // Store the inventory for validation
        customInventories.put(player.getUniqueId(), inv);
        // Open the inventory for the player
        player.openInventory(inv);
    }

    // Utility method to create customized items (glass panes)
    private ItemStack getGlassPaneItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

            List<String> lores = new ArrayList<>();
            for (String s : lore) {
                lores.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            meta.setLore(lores);
            item.setItemMeta(meta);
        }

        return item;
    }
}
