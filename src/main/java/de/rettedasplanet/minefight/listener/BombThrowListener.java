package de.rettedasplanet.minefight.listener;

import de.rettedasplanet.minefight.Minefight;
import de.rettedasplanet.minefight.manager.PointsManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class BombThrowListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand == null || !itemInHand.hasItemMeta()) {
            return;
        }
        String displayName = itemInHand.getItemMeta().getDisplayName();
        if (displayName.contains("Bombe Level")) {
            if (itemInHand.getAmount() > 1) {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            } else {
                player.getInventory().removeItem(itemInHand);
            }
            // Starte das Projektil als Snowball
            Snowball bombProjectile = player.launchProjectile(Snowball.class);
            int explosionMultiplier = 1;
            try {
                // Erwartete Lore-Zeile: "Sprengkraft: X× TNT"
                String loreLine = ChatColor.stripColor(itemInHand.getItemMeta().getLore().get(0));
                String[] parts = loreLine.split(" ");
                String multiplierStr = parts[1].replace("×", "");
                explosionMultiplier = Integer.parseInt(multiplierStr);
            } catch (Exception e) {
                explosionMultiplier = 1;
            }

            bombProjectile.setMetadata("explosionMultiplier", new FixedMetadataValue(Minefight.getInstance(), explosionMultiplier - 0.5));
            player.sendMessage(ChatColor.GRAY + "Du hast eine Bombe geworfen (Sprengkraft: " + explosionMultiplier + "× TNT).");
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball)) {
            return;
        }
        Snowball bomb = (Snowball) event.getEntity();
        if (!bomb.hasMetadata("explosionMultiplier")) {
            return;
        }
        int multiplier = bomb.getMetadata("explosionMultiplier").get(0).asInt();
        float power = 4.0f * multiplier + 1;
        Location explosionCenter = bomb.getLocation();
        World world = bomb.getWorld();

        world.createExplosion(bomb, explosionCenter, power);
        bomb.remove();
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof Snowball && event.getEntity().hasMetadata("explosionMultiplier")) {

            Map<Material, Integer> orePoints = new HashMap<>();
            orePoints.put(Material.IRON_ORE, 10);
            orePoints.put(Material.COAL_ORE, 5);
            orePoints.put(Material.GOLD_ORE, 20);
            orePoints.put(Material.DIAMOND_ORE, 50);
            orePoints.put(Material.EMERALD_ORE, 40);
            orePoints.put(Material.REDSTONE_ORE, 15);
            orePoints.put(Material.LAPIS_ORE, 15);
            orePoints.put(Material.DIRT, 0);

            final List<Material> oreTypes = Arrays.asList(
                    Material.IRON_ORE,
                    Material.COAL_ORE,
                    Material.GOLD_ORE,
                    Material.DIAMOND_ORE,
                    Material.EMERALD_ORE,
                    Material.REDSTONE_ORE,
                    Material.LAPIS_ORE,
                    Material.DIRT
            );

            int totalPointsAwarded = 0;
            Iterator<Block> iterator = event.blockList().iterator();
            while (iterator.hasNext()) {
                Block block = iterator.next();
                if (oreTypes.contains(block.getType())) {
                    totalPointsAwarded += orePoints.get(block.getType());
                    block.setType(Material.AIR, false);
                    iterator.remove();
                } else {
                    iterator.remove();
                }
            }

            Object shooter = ((Snowball) event.getEntity()).getShooter();
            if (shooter instanceof Player) {
                Player player = (Player) shooter;
                if (totalPointsAwarded > 0) {
                    PointsManager.addPoints(player.getUniqueId(), totalPointsAwarded);
                    player.sendMessage(ChatColor.GREEN + "Du hast " + totalPointsAwarded +
                            " Punkte für gesprengte Erze erhalten!");
                    player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                }
            }
        }
    }
}
