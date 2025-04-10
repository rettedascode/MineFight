package de.rettedasplanet.minefight.commands;

import de.rettedasplanet.minefight.Minefight;
import de.rettedasplanet.minefight.manager.GameManager;
import de.rettedasplanet.minefight.runnable.PointsActionBarUpdater;
import de.rettedasplanet.minefight.shop.ShopVillagerSpawner;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StartCommand implements CommandExecutor {

    private final GameManager gameManager;
    private final Plugin plugin;
    private PointsActionBarUpdater actionBarUpdater;

    public StartCommand(GameManager gameManager, Plugin plugin) {
        this.gameManager = gameManager;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen!");
            return true;
        }
        if (!player.hasPermission("minefight.start")) {
            player.sendMessage("Du hast keine Berechtigung, diesen Befehl zu nutzen!");
            return true;
        }
        if (gameManager.isGameRunning()) {
            player.sendMessage("Das Spiel läuft bereits!");
            return true;
        }

        gameManager.startGame();

        BossBar bossBar = Bukkit.createBossBar("Cube wird generiert...", BarColor.BLUE, BarStyle.SOLID);
        for (Player p : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(p);
        }

        Location point1 = Minefight.getInstance().getLocationFromConfig("positions.cube.point1");
        Location point2 = Minefight.getInstance().getLocationFromConfig("positions.cube.point2");

        int minX = Math.min(point1.getBlockX(), point2.getBlockX());
        int minY = Math.min(point1.getBlockY(), point2.getBlockY());
        int minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());

        int maxX = Math.max(point1.getBlockX(), point2.getBlockX());
        int maxY = Math.max(point1.getBlockY(), point2.getBlockY()) - 1;
        int maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());

        int sizeX = maxX - minX + 1;
        int sizeY = maxY - minY + 1;
        int sizeZ = maxZ - minZ + 1;

        int cubeSize = sizeX;
        World world = point1.getWorld();
        Location startLoc = new Location(world, minX, minY, minZ);

        world.setTime(0);
        world.getClearWeatherDuration();
        world.setStorm(false);
        world.setThundering(false);

        generateDirtCube(world, startLoc, cubeSize, bossBar);

        Bukkit.getScheduler().runTaskTimer(plugin, new PointsActionBarUpdater(), 0L, 20L);

        return true;
    }

    private void generateDirtCube(World world, Location start, int size, BossBar bossBar) {
        int startX = start.getBlockX();
        int startY = start.getBlockY();
        int startZ = start.getBlockZ();
        int totalBlocks = size * size * size;
        int[] processedBlocks = new int[]{0};

        // Zähler für jedes Erz und für DIRT
        final int[] countIron = {0};
        final int[] countCoal = {0};
        final int[] countGold = {0};
        final int[] countDiamond = {0};
        final int[] countEmerald = {0};
        final int[] countRedstone = {0};
        final int[] countLapis = {0};
        final int[] countDirt = {0};

        final Random random = new Random();

        double[] oreChances = new double[]{
                0.004,   // Iron Ore: 0,2% (laut Code, Kommentar eventuell anpassen)
                0.015,   // Coal Ore: 1,0%
                0.006,   // Gold Ore: 0,3%
                0.001,  // Diamond Ore: 0,05%
                0.0006,  // Emerald Ore: 0,03%
                0.002,   // Redstone Ore: 0,1%
                0.004    // Lapis Ore: 0,2%
        };

        Material[] ores = new Material[]{
                Material.IRON_ORE,
                Material.COAL_ORE,
                Material.GOLD_ORE,
                Material.DIAMOND_ORE,
                Material.EMERALD_ORE,
                Material.REDSTONE_ORE,
                Material.LAPIS_ORE
        };

        new BukkitRunnable() {
            int x = 0, y = 0, z = 0;
            int tickCounter = 0;

            @Override
            public void run() {
                int blocksPerTick = 10000;
                int processedThisTick = 0;

                while (processedThisTick < blocksPerTick && x < size) {
                    Location blockLoc = new Location(world, startX + x, startY + y, startZ + z);

                    double r = random.nextDouble();
                    double cumulative = 0.0;
                    Material selectedOre = null;
                    for (int i = 0; i < oreChances.length; i++) {
                        cumulative += oreChances[i];
                        if (r < cumulative) {
                            selectedOre = ores[i];
                            break;
                        }
                    }
                    if (selectedOre != null) {
                        blockLoc.getBlock().setType(selectedOre, false);
                        // Zähle das generierte Erz
                        if (selectedOre == Material.IRON_ORE) {
                            countIron[0]++;
                        } else if (selectedOre == Material.COAL_ORE) {
                            countCoal[0]++;
                        } else if (selectedOre == Material.GOLD_ORE) {
                            countGold[0]++;
                        } else if (selectedOre == Material.DIAMOND_ORE) {
                            countDiamond[0]++;
                        } else if (selectedOre == Material.EMERALD_ORE) {
                            countEmerald[0]++;
                        } else if (selectedOre == Material.REDSTONE_ORE) {
                            countRedstone[0]++;
                        } else if (selectedOre == Material.LAPIS_ORE) {
                            countLapis[0]++;
                        }
                    } else {
                        blockLoc.getBlock().setType(Material.DIRT, false);
                        countDirt[0]++;
                    }
                    processedBlocks[0]++;
                    processedThisTick++;

                    z++;
                    if (z >= size) {
                        z = 0;
                        y++;
                        if (y >= size) {
                            y = 0;
                            x++;
                        }
                    }
                }

                tickCounter++;
                if (tickCounter % 2 == 0) {
                    double progress = (double) processedBlocks[0] / totalBlocks;
                    int percent = (int)(progress * 100);
                    bossBar.setProgress(Math.min(progress, 1.0));
                    bossBar.setTitle("Cube wird generiert... " + percent + "%");
                }

                if (processedBlocks[0] >= totalBlocks) {
                    bossBar.setVisible(false);
                    bossBar.removeAll();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.removePotionEffect(PotionEffectType.BLINDNESS);
                    }

                    distributePlayersAndEquip();

                    Bukkit.getServer().broadcastMessage("=============================");
                    Bukkit.getServer().broadcastMessage("Erzeugte Erze:");
                    Bukkit.getServer().broadcastMessage("§7Eisen: " + countIron[0]);
                    Bukkit.getServer().broadcastMessage("§8Kohle: " + countCoal[0]);
                    Bukkit.getServer().broadcastMessage("§6Gold: " + countGold[0]);
                    Bukkit.getServer().broadcastMessage("§bDiamant: " + countDiamond[0]);
                    Bukkit.getServer().broadcastMessage("§aSmaragd: " + countEmerald[0]);
                    Bukkit.getServer().broadcastMessage("§cRedstone: " + countRedstone[0]);
                    Bukkit.getServer().broadcastMessage("§9Lapis: " + countLapis[0]);
                    Bukkit.getServer().broadcastMessage("§eDirt: " + countDirt[0]);
                    Bukkit.getServer().broadcastMessage("=============================");

                    if (actionBarUpdater == null || actionBarUpdater.isCancelled()) {
                        actionBarUpdater = new PointsActionBarUpdater();
                        actionBarUpdater.runTaskTimer(Minefight.getInstance(), 0L, 20L);
                    }

                    cancel();
                }
            }
        }.runTaskTimer(Minefight.getInstance(), 0L, 1L);
    }

    private void distributePlayersAndEquip() {
        final Random random = new Random();

        List<Location> spawnLocations = new ArrayList<>();
        ConfigurationSection spawnsSection = Minefight.getInstance().getConfig().getConfigurationSection("positions.spawns");
        if (spawnsSection != null) {
            for (String key : spawnsSection.getKeys(false)) {
                Location loc = Minefight.getInstance().getLocationFromConfig("positions.spawns." + key);
                if (loc != null) {
                    spawnLocations.add(loc);
                }
            }
        }

        if (spawnLocations.isEmpty()) {
            Bukkit.getLogger().warning("Keine Spawnpunkte in der Config gefunden!");
            return;
        }

        List<Location> availableSpawns = new ArrayList<>(spawnLocations);

        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.PURPLE, Color.ORANGE, Color.LIME, Color.AQUA};

        for (Player p : Minefight.getInstance().getGameManager().getPlayersInGame()) {
            if (availableSpawns.isEmpty()) {
                p.sendMessage("Es sind nicht genügend Spawnpunkte definiert!");
                continue;
            }

            int index = random.nextInt(availableSpawns.size());
            Location spawn = availableSpawns.get(index);
            availableSpawns.remove(index);
            ShopVillagerSpawner.spawnShopVillager(spawn);
            p.teleport(spawn);

            Color randomColor = colors[random.nextInt(colors.length)];

            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
            helmetMeta.setColor(randomColor);
            helmet.setItemMeta(helmetMeta);

            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta chestMeta = (LeatherArmorMeta) chestplate.getItemMeta();
            chestMeta.setColor(randomColor);
            chestplate.setItemMeta(chestMeta);

            ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
            LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
            leggingsMeta.setColor(randomColor);
            leggings.setItemMeta(leggingsMeta);

            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
            bootsMeta.setColor(randomColor);
            boots.setItemMeta(bootsMeta);

            p.getInventory().setHelmet(helmet);
            p.getInventory().setChestplate(chestplate);
            p.getInventory().setLeggings(leggings);
            p.getInventory().setBoots(boots);

            ItemStack shovel = new ItemStack(Material.DIAMOND_SHOVEL);
            shovel.addEnchantment(Enchantment.EFFICIENCY, 5);
            shovel.addEnchantment(Enchantment.UNBREAKING, 3);
            p.getInventory().addItem(shovel);

            ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
            pickaxe.addEnchantment(Enchantment.EFFICIENCY, 5);
            pickaxe.addEnchantment(Enchantment.UNBREAKING, 3);
            p.getInventory().addItem(pickaxe);

            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 2));
        }
    }
}
