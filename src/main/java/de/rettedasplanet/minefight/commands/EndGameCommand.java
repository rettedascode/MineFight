package de.rettedasplanet.minefight.commands;

import de.rettedasplanet.minefight.Minefight;
import de.rettedasplanet.minefight.manager.GameManager;
import de.rettedasplanet.minefight.manager.PointsManager;
import de.rettedasplanet.minefight.shop.ShopVillagerSpawner;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class EndGameCommand implements CommandExecutor {

    private final GameManager gameManager;

    public EndGameCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl ausführen!");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("minefight.endgame") && !player.isOp()) {
            player.sendMessage("Du hast keine Berechtigung, diesen Befehl zu nutzen!");
            return true;
        }

        Location lobby = Minefight.getInstance().getLocationFromConfig("positions.lobby");

        for (Player p : gameManager.getPlayersInGame()) {

            PointsManager.setPoints(p.getUniqueId(),0);
            p.getInventory().clear();
            p.setExp(0);
            p.setLevel(0);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.teleport(lobby);
            p.clearActivePotionEffects();
            p.sendMessage(" Das Spiel ist beendet ");
        }

        ShopVillagerSpawner.removeShopVillagers(player.getWorld());

        gameManager.endGame();

        Location point1 = Minefight.getInstance().getLocationFromConfig("positions.cube.point1");
        Location point2 = Minefight.getInstance().getLocationFromConfig("positions.cube.point2");

        int minX = Math.min(point1.getBlockX(), point2.getBlockX());
        int minY = Math.min(point1.getBlockY(), point2.getBlockY());
        int minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());
        int maxX = Math.max(point1.getBlockX(), point2.getBlockX());
        int maxY = Math.max(point1.getBlockY(), point2.getBlockY());
        int maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());
        World world = point1.getWorld();

        int totalBlocks = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);

        BossBar bossBar = Bukkit.createBossBar("Cube wird geleert... 0%", BarColor.RED, BarStyle.SOLID);
        for (Player p : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(p);
        }

        new BukkitRunnable() {
            int x = minX, y = minY, z = minZ;
            int clearedBlocks = 0;

            @Override
            public void run() {
                int blocksPerTick = 10000;
                int processed = 0;
                while (processed < blocksPerTick) {
                    if (x > maxX) {
                        break;
                    }
                    Location blockLoc = new Location(world, x, y, z);
                    blockLoc.getBlock().setType(Material.AIR, false);
                    clearedBlocks++;
                    processed++;

                    z++;
                    if (z > maxZ) {
                        z = minZ;
                        y++;
                        if (y > maxY) {
                            y = minY;
                            x++;
                        }
                    }
                }
                double progress = (double) clearedBlocks / totalBlocks;
                int percent = (int) (progress * 100);
                bossBar.setProgress(Math.min(progress, 1.0));
                bossBar.setTitle("Cube wird geleert... " + percent + "%");

                if (clearedBlocks >= totalBlocks || x > maxX) {
                    bossBar.setVisible(false);
                    bossBar.removeAll();
                    cancel();
                }
            }
        }.runTaskTimer(Minefight.getInstance(), 0L, 1L);

        if (player.isOp()) {
            ItemStack startItem = new ItemStack(Material.LIME_DYE);
            ItemMeta meta = startItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.GREEN + "Spiel starten");
                startItem.setItemMeta(meta);
            }
            player.getInventory().setItem(4, startItem);
        }

        return true;
    }
}
