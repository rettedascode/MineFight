package de.rettedasplanet.minefight.listener;

import de.rettedasplanet.minefight.manager.PointsManager;
import de.rettedasplanet.minefight.Minefight;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class OrePointsListener implements Listener {

    @EventHandler
    public void onOreBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        int pointsAward = 0;
        switch (type) {
            case IRON_ORE:
                pointsAward = 5;
                break;
            case COAL_ORE:
                pointsAward = 2;
                break;
            case GOLD_ORE:
                pointsAward = 8;
                break;
            case DIAMOND_ORE:
                pointsAward = 20;
                break;
            case EMERALD_ORE:
                pointsAward = 25;
                break;
            case DIRT:
                event.setDropItems(false);
                break;
            case REDSTONE_ORE:
                pointsAward = 4;
                break;
            case LAPIS_ORE:
                pointsAward = 3;
                break;
            default:
                break;
        }
        if (pointsAward <= 0) {
            return;
        }
        event.setDropItems(false);
        Player player = event.getPlayer();
        PointsManager.addPoints(player.getUniqueId(), pointsAward);
        int totalPoints = PointsManager.getPoints(player.getUniqueId());
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aPunkte: §7" + totalPoints));
        spawnPointsHologram(block.getLocation(), pointsAward);
    }


    @EventHandler
    private void BreakPrevention(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        if (type == Material.GLASS || type == Material.OAK_LOG || type == Material.OAK_PLANKS) {
            event.setCancelled(true);
        }
    }

    private void spawnPointsHologram(Location location, int points) {
        Location hologramLoc = location.clone().add(0.5, 1.2, 0.5);
        if (hologramLoc.getWorld() == null) return;

        ArmorStand hologram = hologramLoc.getWorld().spawn(hologramLoc, ArmorStand.class);
        hologram.setGravity(false);
        hologram.setVisible(false);
        hologram.setMarker(true);
        hologram.setCustomNameVisible(true);
        hologram.setCustomName(ChatColor.GOLD + "+" + points + " Punkte");

        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (hologram.isDead() || ticks >= 30) {
                    hologram.remove();
                    cancel();
                    return;
                }
                Location currentLoc = hologram.getLocation();
                currentLoc.add(0, 0.05, 0);
                hologram.teleport(currentLoc);
                ticks++;
            }
        }.runTaskTimer(Minefight.getInstance(), 0L, 1L);
    }
}
