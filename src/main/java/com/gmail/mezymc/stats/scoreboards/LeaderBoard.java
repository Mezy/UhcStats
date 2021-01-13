package com.gmail.mezymc.stats.scoreboards;

import com.gmail.mezymc.stats.GameMode;
import com.gmail.mezymc.stats.StatType;
import com.gmail.mezymc.stats.StatsManager;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;

public class LeaderBoard{

    private String key;
    private StatType statType;
    private List<BoardPosition> boardPositions;
    private GameMode gameMode;
    private Location location;
    private String format;

    private ArmorStand armorStand1;
    private ArmorStand armorStand2;

    public LeaderBoard(String key, StatType statType, GameMode gameMode){
        this.key = key;
        this.statType = statType;
        this.gameMode = gameMode;
    }

    public StatType getStatType() {
        return statType;
    }

    public Location getLocation() {
        return location;
    }

    public void instantiate(ConfigurationSection cfg){

        String title = cfg.getString("title");
        format = cfg.getString("lines");

        boolean didFindWorld = false;
        World worldForLeaderboard = Bukkit.getWorlds().get(0);
        String configWorldName = cfg.getString("location.world");

        // If this leader-board specifies which world it should be created in, create it in that world
        if(configWorldName != null) {
            worldForLeaderboard = Bukkit.getWorld(configWorldName);
            if(worldForLeaderboard == null) {
                Bukkit.getLogger().warning("[UhcStats] World \"" + configWorldName + "\" for leaderboard titled \"" + title + "\" is invalid");
                Bukkit.getLogger().warning("[UhcStats] Will attempt to place it in default lobby world...");
            } else {
                didFindWorld = true;
            }
        }
        // Otherwise, search for the world containing the default lobby to place the leader-board into
        if(!didFindWorld) {
            for (World world : Bukkit.getWorlds()) {
                // The world must be in the overworld
                if (world.getEnvironment() == World.Environment.NORMAL) {
                    // The correct one is the one with the glass box (the default lobby)
                    if (world.getBlockAt(0, 199, 0).getType() == Material.GLASS) {
                        worldForLeaderboard = world;
                        didFindWorld = true;
                    }
                }
            }
        }
        // If the correct world could not be found for leader-board, issue a warning
        if(!didFindWorld) {
            Bukkit.getLogger().warning("[UhcStats] Could not find the default lobby world for leaderboard titled \"" + title + "\"");
            Bukkit.getLogger().warning("[UhcStats] Using the first available one instead");
        }

        location = new Location(
                worldForLeaderboard,
                cfg.getDouble("location.x"),
                cfg.getDouble("location.y"),
                cfg.getDouble("location.z")
        );

        title = ChatColor.translateAlternateColorCodes('&', title);
        format = ChatColor.translateAlternateColorCodes('&', format);

        armorStand1 = spawnArmorStand(
                new Location(location.getWorld(), location.getX(), location.getY() - .3, location.getZ()),
                title
        );
        armorStand2 = null;

    }

    public String getFormat(){
        return format;
    }

    private ArmorStand spawnArmorStand(Location location, String text){

        ArmorStand armorStand = null;

        for (Entity entity : location.getWorld().getEntities()){
            if (entity.getType() == EntityType.ARMOR_STAND && entity.getLocation().equals(location)){
                armorStand = (ArmorStand) entity;
            }
        }

        if (armorStand == null){
            armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        }

        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(text);

        return armorStand;
    }

    public void unload(){
        if (armorStand1 != null) {
            armorStand1.remove();
        }
        if (armorStand2 != null) {
            armorStand2.remove();
        }

        if (boardPositions == null){
            return;
        }

        for (BoardPosition boardPosition : boardPositions){
            boardPosition.remove();
        }
    }

    public void update(){
        boardPositions = StatsManager.getStatsManager().getTop10(this, statType, gameMode);
        boardPositions.forEach(BoardPosition::updateText);
    }

    public BoardPosition getBoardPosition(int position){
        if (boardPositions == null) return null;
        for (BoardPosition boardPosition : boardPositions){
            if (boardPosition.getPosition() == position){
                return boardPosition;
            }
        }
        return null;
    }

}