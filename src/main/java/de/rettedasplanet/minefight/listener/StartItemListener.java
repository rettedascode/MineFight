package de.rettedasplanet.minefight.listener;

import de.rettedasplanet.minefight.manager.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StartItemListener implements Listener {

    private final GameManager GameManager = new GameManager();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) {
            ItemStack startItem = new ItemStack(Material.LIME_DYE);
            ItemMeta meta = startItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.GREEN + "Spiel starten");
                startItem.setItemMeta(meta);
            }
            player.getInventory().setItem(4, startItem);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.LIME_DYE) {
            return;
        }

        player.getInventory().setItemInMainHand(null);

        player.performCommand("startgame");

        event.setCancelled(true);
    }
}
