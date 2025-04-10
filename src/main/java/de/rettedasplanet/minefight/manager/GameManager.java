package de.rettedasplanet.minefight.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameManager {

    // Maximale Spieleranzahl im Spiel
    private final int MAX_PLAYERS = 8;

    // Liste der Spieler, die am Spiel teilnehmen
    private final List<Player> playersInGame;

    // Kennzeichnet, ob das Spiel läuft oder nicht
    private boolean gameRunning;

    public GameManager() {
        this.playersInGame = new ArrayList<>();
        this.gameRunning = false;
    }

    /**
     * Fügt einen Spieler dem Spiel hinzu.
     *
     * @param player Der Spieler, der hinzugefügt werden soll.
     */
    public void addPlayer(Player player) {
        if (playersInGame.contains(player)) {
            player.sendMessage("Du bist bereits im Spiel!");
            return;
        }

        if (playersInGame.size() >= MAX_PLAYERS) {
            player.sendMessage("Das Spiel ist voll! Maximal " + MAX_PLAYERS + " Spieler erlaubt.");
            return;
        }

        playersInGame.add(player);
        Bukkit.getLogger().info(player.getName() + " wurde dem Spiel hinzugefügt. (Aktuell: " + playersInGame.size() + " Spieler)");
    }

    public void removePlayer(Player player) {
        if (playersInGame.remove(player)) {
            Bukkit.getLogger().info(player.getName() + " hat das Spiel verlassen. (Aktuell: " + playersInGame.size() + " Spieler)");
        }
    }

    public List<Player> getPlayersInGame() {
        return playersInGame;
    }

    public void startGame() {
        if (gameRunning) {
            Bukkit.getLogger().info("Das Spiel läuft bereits!");
            return;
        }

        gameRunning = true;
        Bukkit.getLogger().info("Spiel gestartet!");
    }

    public void endGame() {
        if (!gameRunning) {
            Bukkit.getLogger().info("Das Spiel läuft aktuell nicht.");
            return;
        }
        gameRunning = false;
        Bukkit.getLogger().info("Spiel beendet!");

        // Hier könntest du z.B. auch die Spieler-Liste leeren oder in den Lobby-Zustand zurückversetzen.
        // playersInGame.clear();
    }

    /**
     * Prüft, ob das Spiel aktuell läuft.
     * @return true, wenn das Spiel gestartet wurde, sonst false.
     */
    public boolean isGameRunning() {
        return gameRunning;
    }
}
