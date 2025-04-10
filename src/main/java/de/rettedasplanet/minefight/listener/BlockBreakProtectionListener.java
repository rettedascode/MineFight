package de.rettedasplanet.minefight.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BlockBreakProtectionListener implements Listener {
    private static final Set<Material> RESTRICTED_BLOCKS = new HashSet<>(Arrays.asList(
            Material.GLASS,
            Material.OAK_PLANKS,
            Material.OAK_LOG,
            Material.GLOWSTONE
    ));

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        Player player = event.getPlayer();

        if(player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(false);
        }
        if (RESTRICTED_BLOCKS.contains(type)) {
            event.setCancelled(true);
        }
    }
}
