package de.rettedasplanet.minefight.listener;

import de.rettedasplanet.minefight.Minefight;
import de.rettedasplanet.minefight.handler.ScoreboardHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("§7[§a+§7] §8" + player.getName());
        Location lobby = Minefight.getInstance().getLocationFromConfig("positions.lobby");
        if (lobby != null) {
            player.teleport(lobby);
            player.sendMessage("Willkommen in der Lobby!");
        } else {
            player.sendMessage("Lobby-Position ist nicht gesetzt. Bitte nutze /minefightsetup lobby um diese festzulegen!");
        }
        Minefight.getInstance().getGameManager().addPlayer(player);
        ScoreboardHandler sbHandler = Minefight.getInstance().getScoreboardHandler();
        int playerCount = Minefight.getInstance().getGameManager().getPlayersInGame().size();
        String cubeSize = "128x128x128";
        sbHandler.updateScoreboard(playerCount, cubeSize);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Minefight.getInstance().getGameManager().removePlayer(player);
        event.setQuitMessage("§7[§c-§7] §8" + player.getName());

    }
}
