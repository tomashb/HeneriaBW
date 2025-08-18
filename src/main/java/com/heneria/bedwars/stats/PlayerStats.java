package com.heneria.bedwars.stats;

import java.util.UUID;

/**
 * Simple data holder for player statistics kept in memory.
 */
public class PlayerStats {

    private final UUID uuid;
    private String username;
    private int kills;
    private int deaths;
    private int wins;
    private int losses;
    private int bedsBroken;
    private int gamesPlayed;

    public PlayerStats(UUID uuid, String username) {
        this(uuid, username, 0, 0, 0, 0, 0, 0);
    }

    public PlayerStats(UUID uuid, String username, int kills, int deaths, int wins,
                       int losses, int bedsBroken, int gamesPlayed) {
        this.uuid = uuid;
        this.username = username;
        this.kills = kills;
        this.deaths = deaths;
        this.wins = wins;
        this.losses = losses;
        this.bedsBroken = bedsBroken;
        this.gamesPlayed = gamesPlayed;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getKills() {
        return kills;
    }

    public void incrementKills() {
        this.kills++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void incrementDeaths() {
        this.deaths++;
    }

    public int getWins() {
        return wins;
    }

    public void incrementWins() {
        this.wins++;
    }

    public int getLosses() {
        return losses;
    }

    public void incrementLosses() {
        this.losses++;
    }

    public int getBedsBroken() {
        return bedsBroken;
    }

    public void incrementBedsBroken() {
        this.bedsBroken++;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }
}

