package com.gmail.mezymc.stats.scoreboards;

import com.gmail.mezymc.stats.StatsManager;
import com.gmail.mezymc.stats.UhcStats;
import org.bukkit.Bukkit;

public class LeaderboardUpdateThread implements Runnable{

    private StatsManager statsManager;

    public LeaderboardUpdateThread(StatsManager statsManager){
        this.statsManager = statsManager;
    }

    @Override
    public void run() {
        for (LeaderBoard leaderBoard : statsManager.getLeaderBoards()){
            try {
                leaderBoard.update();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        // Re-run if need be
        int leaderboardsUpdateInterval = statsManager.getLeaderBoardsUpdateInterval();
        if(leaderboardsUpdateInterval > 0) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(UhcStats.getPlugin(), this, 20 * leaderboardsUpdateInterval);
        }
    }

    public static void runSync(Runnable runnable){
        Bukkit.getScheduler().runTask(UhcStats.getPlugin(), runnable);
    }

}