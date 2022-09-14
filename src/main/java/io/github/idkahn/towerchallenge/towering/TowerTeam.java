package io.github.idkahn.towerchallenge.towering;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class TowerTeam {

    // Server's scoreboard
    private final Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

    private Team team;

    public TowerTeam(String name) {
        team = scoreboard.registerNewTeam(name);
        
    }

}
