package com.heneria.bedwars.arena.elements;

import com.heneria.bedwars.arena.enums.TeamColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a team within an arena.
 */
public class Team {

    private final TeamColor color;
    private final List<UUID> members = new ArrayList<>();
    private Location spawnLocation;
    private Location bedLocation;
    private Location itemShopNpcLocation;
    private Location upgradeShopNpcLocation;
    private boolean hasBed = true;
    private final Map<String, Integer> upgradeLevels = new HashMap<>();

    /**
     * Creates a new team with the given color.
     *
     * @param color the team color
     */
    public Team(TeamColor color) {
        this.color = color;
    }

    /**
     * Gets the color of the team.
     *
     * @return the team color
     */
    public TeamColor getColor() {
        return color;
    }

    /**
     * Gets the members of the team.
     *
     * @return list of members
     */
    public List<UUID> getMembers() {
        return members;
    }

    /**
     * Adds a player to the team.
     *
     * @param uuid player's unique id
     */
    public void addMember(UUID uuid) {
        members.add(uuid);
    }

    /**
     * Removes a player from the team.
     *
     * @param uuid player's unique id
     */
    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    /**
     * Checks if a player is part of the team.
     *
     * @param uuid player's unique id
     * @return true if the player is in the team
     */
    public boolean isMember(UUID uuid) {
        return members.contains(uuid);
    }

    /**
     * Gets the spawn location of the team.
     *
     * @return the spawn location
     */
    public Location getSpawnLocation() {
        return spawnLocation;
    }

    /**
     * Sets the spawn location of the team.
     *
     * @param spawnLocation the new spawn location
     */
    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    /**
     * Gets the location of the team's bed.
     *
     * @return the bed location
     */
    public Location getBedLocation() {
        return bedLocation;
    }

    /**
     * Sets the location of the team's bed.
     *
     * @param bedLocation the bed location
     */
    public void setBedLocation(Location bedLocation) {
        this.bedLocation = bedLocation;
    }

    /**
     * Gets the location of the team's item shop NPC.
     *
     * @return the item shop NPC location
     */
    public Location getItemShopNpcLocation() {
        return itemShopNpcLocation;
    }

    /**
     * Sets the location of the team's item shop NPC.
     *
     * @param itemShopNpcLocation location of the NPC
     */
    public void setItemShopNpcLocation(Location itemShopNpcLocation) {
        this.itemShopNpcLocation = itemShopNpcLocation;
    }

    /**
     * Gets the location of the team's upgrade shop NPC.
     *
     * @return the upgrade shop NPC location
     */
    public Location getUpgradeShopNpcLocation() {
        return upgradeShopNpcLocation;
    }

    /**
     * Sets the location of the team's upgrade shop NPC.
     *
     * @param upgradeShopNpcLocation location of the NPC
     */
    public void setUpgradeShopNpcLocation(Location upgradeShopNpcLocation) {
        this.upgradeShopNpcLocation = upgradeShopNpcLocation;
    }

    /**
     * Checks whether the team still has its bed.
     *
     * @return true if the bed is intact
     */
    public boolean hasBed() {
        return hasBed;
    }

    /**
     * Sets whether the team has a bed.
     *
     * @param hasBed new bed state
     */
    public void setHasBed(boolean hasBed) {
        this.hasBed = hasBed;
    }

    /**
     * Gets the current level of a team upgrade.
     *
     * @param id the upgrade id
     * @return the level, or {@code 0} if not purchased
     */
    public int getUpgradeLevel(String id) {
        return upgradeLevels.getOrDefault(id, 0);
    }

    /**
     * Sets the level of a team upgrade.
     *
     * @param id    the upgrade id
     * @param level the new level
     */
    public void setUpgradeLevel(String id, int level) {
        upgradeLevels.put(id, level);
    }
}
