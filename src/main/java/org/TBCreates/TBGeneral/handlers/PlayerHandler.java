package org.TBCreates.TBGeneral.handlers;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.TBCreates.TBGeneral.Listeners.UpdateChecker; // Import UpdateChecker

public class PlayerHandler implements Listener {

    private final JavaPlugin plugin;
    private final UpdateChecker updateChecker;

    public PlayerHandler(JavaPlugin plugin, UpdateChecker updateChecker) {
        this.plugin = plugin;
        this.updateChecker = updateChecker; // Store UpdateChecker instance
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        NamespacedKey key = new NamespacedKey(plugin, "received_tb_general_book");

        // Check if the player has already received the book
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        if (!dataContainer.has(key, PersistentDataType.BYTE)) {
            // Create a written book
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
            BookMeta bookMeta = (BookMeta) book.getItemMeta();

            // Set the title and author
            bookMeta.setTitle("TBGeneral Info");
            bookMeta.setAuthor("TheBestHarrison");

            // Add clickable link
            String content = "This plugin is great!\n\n" +
                    "Click the link below to visit:\n\n" +
                    "§n§9https://github.com/Thebestharrison1221/TBGeneral";
            bookMeta.addPage(content);

            // Apply metadata back to the book
            book.setItemMeta(bookMeta);

            // Add the book to the player's inventory
            Inventory inv = player.getInventory();
            inv.addItem(book);

            // Mark the player as having received the book
            dataContainer.set(key, PersistentDataType.BYTE, (byte) 1);
        }

        // If the player has the permission, check for updates
        if (player.hasPermission("tbgeneral.updatemsg")) {
            updateChecker.checkForUpdatesForPlayer(player);
        }
    }
}
