package com.heneria.bedwars.arena.elements;

import com.heneria.bedwars.arena.enums.TeamColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private Material itemShopChestplate;
    private Material itemShopLeggings;
    private Material itemShopBoots;
    private Location upgradeShopNpcLocation;
    private Material upgradeShopChestplate;
    private Material upgradeShopLeggings;
    private Material upgradeShopBoots;
    private boolean hasBed = true;
    private final Map<String, Integer> upgradeLevels = new HashMap<>();
    private final Map<String, Boolean> traps = new LinkedHashMap<>();

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

    public Material getItemShopChestplate() {
        return itemShopChestplate;
    }

    public void setItemShopChestplate(Material itemShopChestplate) {
        this.itemShopChestplate = itemShopChestplate;
    }

    public Material getItemShopLeggings() {
        return itemShopLeggings;
    }

    public void setItemShopLeggings(Material itemShopLeggings) {
        this.itemShopLeggings = itemShopLeggings;
    }

    public Material getItemShopBoots() {
        return itemShopBoots;
    }

    public void setItemShopBoots(Material itemShopBoots) {
        this.itemShopBoots = itemShopBoots;
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

    public Material getUpgradeShopChestplate() {
        return upgradeShopChestplate;
    }

    public void setUpgradeShopChestplate(Material upgradeShopChestplate) {
        this.upgradeShopChestplate = upgradeShopChestplate;
    }

    public Material getUpgradeShopLeggings() {
        return upgradeShopLeggings;
    }

    public void setUpgradeShopLeggings(Material upgradeShopLeggings) {
        this.upgradeShopLeggings = upgradeShopLeggings;
    }

    public Material getUpgradeShopBoots() {
        return upgradeShopBoots;
    }

    public void setUpgradeShopBoots(Material upgradeShopBoots) {
        this.upgradeShopBoots = upgradeShopBoots;
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

    /**
     * Checks whether the team has purchased the Heal Pool upgrade.
     *
     * @return {@code true} if the team owns a Heal Pool
     */
    public boolean hasHealPool() {
        return getUpgradeLevel("heal-pool") > 0;
    }

    /**
     * Checks whether the team has purchased the Trap Alarm upgrade.
     *
     * @return {@code true} if the team owns a Trap Alarm
     */
    public boolean hasTrapAlarm() {
        return getUpgradeLevel("trap-alarm") > 0;
    }

    /**
     * Checks whether the specified trap is active for this team.
     *
     * @param id trap identifier
     * @return {@code true} if the trap has been purchased and not yet triggered
     */
    public boolean isTrapActive(String id) {
        return traps.getOrDefault(id, false);
    }

    /**
     * Sets the active state of a trap for this team.
     *
     * @param id     trap identifier
     * @param active whether the trap is active
     */
    public void setTrapActive(String id, boolean active) {
        traps.put(id, active);
    }

    /**
     * Resets all upgrades and traps for this team.
     */
     public void resetUpgrades() {
         upgradeLevels.clear();
         traps.clear();
     }

    /**
     * Gets all traps and their active state for this team.
     *
     * @return map of traps
     */
    public Map<String, Boolean> getTraps() {
        return traps;
    }
}
