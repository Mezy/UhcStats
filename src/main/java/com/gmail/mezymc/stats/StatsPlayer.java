package com.gmail.mezymc.stats;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

public class StatsPlayer{

    private String id;
    private Map<String, Map<StatType, Integer>> gameModeStats;
    private boolean needsPush;

    public StatsPlayer(String id){
        this.id = id;
        gameModeStats = new HashMap<>();
        needsPush = false;
    }

    public String getId() {
        return id;
    }

    public boolean isNeedsPush() {
        return needsPush;
    }

    public void setNeedsPush(boolean needsPush) {
        this.needsPush = needsPush;
    }

    void setGameModeStats(GameMode gameMode, Map<StatType, Integer> stats){
        gameModeStats.put(gameMode.getKey(), stats);
    }

    public int getGameModePoints(GameMode gameMode){
        YamlConfiguration cfg = StatsManager.getStatsManager().getConfig();
        int kills = getGameModeStats(GameMode.DEFAULT).getOrDefault(StatType.KILL, -1);
        int wins = getGameModeStats(GameMode.DEFAULT).getOrDefault(StatType.WIN, -1);
        int points = 0;
        if (kills > 0){
            points += kills * cfg.getInt("points.kill-points");
        }
        if (wins > 0){
            points += wins * cfg.getInt("points.win-points");
        }
        return points;
    }

    public Map<StatType, Integer> getGameModeStats(GameMode gameMode){
        return gameModeStats.get(gameMode.getKey());
    }

    public void addOneToStats(GameMode gameMode, StatType statType){
        Map<StatType, Integer> stats = getGameModeStats(gameMode);
        stats.put(statType, stats.get(statType) + 1);
        gameModeStats.put(gameMode.getKey(), stats);
        needsPush = true;
    }

}