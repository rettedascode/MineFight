package de.rettedasplanet.minefight.shop;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

public class ShopVillagerSpawner {

    public static void spawnShopVillager(Location location) {
        Villager shopVillager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        shopVillager.setCustomName("§6Base Shop");
        shopVillager.setCustomNameVisible(true);
        shopVillager.setAI(false);
        shopVillager.setInvulnerable(true);
        shopVillager.setCanPickupItems(false);
        shopVillager.setSilent(true);
    }

    public static void removeShopVillagers(World world) {
        if (world == null) {
            return;
        }
        for (Entity entity : world.getEntities()) {
            if (entity.getType() == EntityType.VILLAGER) {
                if (entity instanceof Villager villager) {
                    if (villager.getCustomName() != null && villager.getCustomName().equals("§6Base Shop")) {
                        villager.remove();
                    }
                }
            }
        }
    }


}
