package de.rettedasplanet.minefight.commands;

import de.rettedasplanet.minefight.utils.CubeSelection;
import de.rettedasplanet.minefight.manager.CubeSelectionManager;
import de.rettedasplanet.minefight.Minefight;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class SetupCommand implements CommandExecutor {

    private final Minefight plugin;

    public SetupCommand(Minefight plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Ingame-Spieler können diesen Befehl verwenden!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("minefight.setup")) {
            player.sendMessage("Du hast keine Berechtigung für diesen Befehl!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cBenutze: /minefightsetup <lobby|spawn|cube>");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        Location loc = player.getLocation();
        World world = loc.getWorld();
        if (world == null) {
            player.sendMessage("Welt konnte nicht ermittelt werden!");
            return true;
        }

        switch (subCommand) {
            case "lobby":
                saveLocationToConfig("positions.lobby", loc);
                player.sendMessage("§aLobby-Position gespeichert!");
                break;

            case "spawn":
                if (args.length < 2) {
                    player.sendMessage("§cBitte gib eine Spawn-Nummer an: /minefightsetup spawn <nummer>");
                    return true;
                }
                String spawnNumber = args[1];  // z. B. "1"
                String spawnPath = "positions.spawns." + spawnNumber;
                saveLocationToConfig(spawnPath, loc);
                player.sendMessage("§aSpawn #" + spawnNumber + " wurde gespeichert!");
                break;

            case "cube":
                // Cube-Subkommandos: select und confirm
                if (args.length < 2) {
                    player.sendMessage("§cBenutze: /minefightsetup cube <select|confirm>");
                    return true;
                }
                String cubeSub = args[1].toLowerCase();
                if (cubeSub.equals("select")) {
                    // Auswahl-Tool (z. B. ein Stick) erstellen und ins Inventar geben
                    ItemStack selector = new ItemStack(Material.STICK);
                    ItemMeta meta = selector.getItemMeta();
                    meta.setDisplayName(ChatColor.GOLD + "Cube Selector");
                    selector.setItemMeta(meta);
                    player.getInventory().addItem(selector);
                    player.sendMessage("§aCube Selector wurde deinem Inventar hinzugefügt.");
                    player.sendMessage("§aKlicke mit der linken Maustaste für Punkt 1 und mit der rechten Maustaste für Punkt 2.");
                } else if (cubeSub.equals("confirm")) {
                    // Auswahl bestätigen und in der Config speichern
                    CubeSelection selection = CubeSelectionManager.getSelection(player.getUniqueId());
                    if (selection == null || !selection.isComplete()) {
                        player.sendMessage("§cDu hast nicht beide Punkte gesetzt!");
                    } else {
                        saveLocationToConfig("positions.cube.point1", selection.getPointOne());
                        saveLocationToConfig("positions.cube.point2", selection.getPointTwo());
                        player.sendMessage("§aCube-Auswahl wurde gespeichert!");
                        CubeSelectionManager.clearSelection(player.getUniqueId());
                    }
                } else {
                    player.sendMessage("§cUnbekannter Sub-Befehl für cube: " + cubeSub);
                    player.sendMessage("§cBenutze: /minefightsetup cube <select|confirm>");
                }
                break;

            default:
                player.sendMessage("§cUnbekannter Befehl: " + subCommand);
                player.sendMessage("§cVerwende /minefightsetup <lobby|spawn|cube>");
                break;
        }

        return true;
    }

    /**
     * Hilfsmethode, um eine Location in der Config zu speichern.
     */
    private void saveLocationToConfig(String path, Location loc) {
        plugin.getConfig().set(path + ".world", loc.getWorld().getName());
        plugin.getConfig().set(path + ".x", loc.getX());
        plugin.getConfig().set(path + ".y", loc.getY());
        plugin.getConfig().set(path + ".z", loc.getZ());
        plugin.getConfig().set(path + ".yaw", loc.getYaw());
        plugin.getConfig().set(path + ".pitch", loc.getPitch());
        plugin.saveConfig();
    }
}
