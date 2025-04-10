package de.rettedasplanet.minefight.handler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardHandler {

    private final Scoreboard scoreboard;
    private final Objective objective;

    public ScoreboardHandler() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("minefight", "dummy", ChatColor.BOLD + " MineFight ");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void updateScoreboard(int playerCount, String cubeSize) {
        String playerLine = ChatColor.YELLOW + "Spieler: " + ChatColor.WHITE + playerCount;
        String cubeLine = ChatColor.GREEN + "Cube Size: " + ChatColor.WHITE + cubeSize;

        scoreboard.resetScores(playerLine);
        scoreboard.resetScores(cubeLine);


        Score placeholderScore2 = objective.getScore("§7=====================");
        placeholderScore2.setScore(5);
        Score playerScore = objective.getScore(playerLine);
        playerScore.setScore(4);
        Score placeholderScore = objective.getScore("§7=====================");
        playerScore.setScore(3);
        Score cubeScore = objective.getScore(cubeLine);
        cubeScore.setScore(2);
        Score placerholder3 = objective.getScore("§7=====================");
        placerholder3.setScore(1);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(scoreboard);
        }
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
