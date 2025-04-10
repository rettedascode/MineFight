package de.rettedasplanet.minefight.listener;

import de.rettedasplanet.minefight.manager.CubeSelectionManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CubeSelectionListener implements Listener {

    public static final String SELECTOR_NAME = ChatColor.GOLD + "Cube Selector";

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;
        if (!meta.getDisplayName().equals(SELECTOR_NAME)) return;

        event.setCancelled(true);

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            CubeSelectionManager.setPointOne(event.getPlayer().getUniqueId(), event.getClickedBlock().getLocation());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Cube Selection - Punkt 1 gesetzt!");
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            CubeSelectionManager.setPointTwo(event.getPlayer().getUniqueId(), event.getClickedBlock().getLocation());
            event.getPlayer().sendMessage(ChatColor.GREEN + "Cube Selection - Punkt 2 gesetzt!");
        }
    }
}
