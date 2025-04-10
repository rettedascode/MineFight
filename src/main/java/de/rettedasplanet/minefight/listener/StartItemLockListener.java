package de.rettedasplanet.minefight.listener;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class StartItemLockListener implements Listener {

    private final String lockedItemName = ChatColor.GREEN + "Spiel starten";

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack current = event.getCurrentItem();
        if (current == null || !current.hasItemMeta()) return;
        if (!current.getItemMeta().hasDisplayName()) return;
        String displayName = current.getItemMeta().getDisplayName();
        if (displayName.equals(lockedItemName)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        for (ItemStack item : event.getNewItems().values()) {
            if (item == null || !item.hasItemMeta()) continue;
            if (!item.getItemMeta().hasDisplayName()) continue;
            String displayName = item.getItemMeta().getDisplayName();
            if (displayName.equals(lockedItemName)) {
                event.setCancelled(true);
                break;
            }
        }
    }
}
