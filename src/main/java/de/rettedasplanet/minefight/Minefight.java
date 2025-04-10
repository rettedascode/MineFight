package de.rettedasplanet.minefight;

import de.rettedasplanet.minefight.commands.EndGameCommand;
import de.rettedasplanet.minefight.commands.SetupCommand;
import de.rettedasplanet.minefight.commands.StartCommand;
import de.rettedasplanet.minefight.handler.ScoreboardHandler;
import de.rettedasplanet.minefight.listener.*;
import de.rettedasplanet.minefight.manager.GameManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Minefight extends JavaPlugin {

    private static  Minefight instance;
    private static GameManager gameManager;
    private static ScoreboardHandler scoreboardHandler;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        gameManager = new GameManager();
        scoreboardHandler = new ScoreboardHandler();

        Objects.requireNonNull(getCommand("setup")).setExecutor(new SetupCommand(this));
        Objects.requireNonNull(getCommand("startgame")).setExecutor(new StartCommand(gameManager, this));
        Objects.requireNonNull(getCommand("endgame")).setExecutor(new EndGameCommand(gameManager));
        getServer().getPluginManager().registerEvents(new CubeSelectionListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new StartItemListener(), this);
        getServer().getPluginManager().registerEvents(new OrePointsListener(), this);
        getServer().getPluginManager().registerEvents(new StartItemLockListener(), this);
        getServer().getPluginManager().registerEvents(new BombThrowListener(), this);
        getServer().getPluginManager().registerEvents(new ShopVillagerListener(), this);

        getLogger().info("Minefight-Plugin aktiviert!");

    }

    public Location getLocationFromConfig(String path) {
        String worldName = getConfig().getString(path + ".world");
        if (worldName == null) {
            return null;
        }

        World world = getServer().getWorld(worldName);
        if (world == null) {
            return null;
        }

        double x = getConfig().getDouble(path + ".x");
        double y = getConfig().getDouble(path + ".y");
        double z = getConfig().getDouble(path + ".z");
        float yaw = (float) getConfig().getDouble(path + ".yaw");
        float pitch = (float) getConfig().getDouble(path + ".pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public void onDisable() {
        getLogger().info("MineFight-Plugin deaktiviert!");
    }

    public static Minefight getInstance() {
        return instance;
    }

    public ScoreboardHandler getScoreboardHandler() {
        return scoreboardHandler;
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
