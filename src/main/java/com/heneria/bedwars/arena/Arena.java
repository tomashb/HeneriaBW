package com.heneria.bedwars.arena;

import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.arena.enums.TeamColor;
import org.bukkit.Location;

import java.util.*;

/**
 * Represents a BedWars arena with its configuration and runtime state.
 */
public class Arena {

    private final String name;
    private GameState state = GameState.WAITING;
    private String worldName;
    private boolean enabled;
    private int minPlayers;
    private int maxPlayers;
    private final List<UUID> players = new ArrayList<>();
    private final Map<TeamColor, Team> teams = new EnumMap<>(TeamColor.class);
    private final List<Generator> generators = new ArrayList<>();
    private Location lobbyLocation;
    private Location shopNpcLocation;
    private Location upgradeNpcLocation;

    /**
     * Creates a new arena with the given name.
     *
     * @param name the arena name
     */
    public Arena(String name) {
        this.name = name;
    }

    /**
     * Gets the arena name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the current state of the arena.
     *
     * @return the game state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Sets the current state of the arena.
     *
     * @param state the new state
     */
    public void setState(GameState state) {
        this.state = state;
    }

    /**
     * Checks whether the arena is enabled for play.
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the arena is enabled for play.
     *
     * @param enabled new enabled state
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the name of the world used by the arena.
     *
     * @return the world name
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Sets the world used by the arena.
     *
     * @param worldName the world name
     */
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    /**
     * Gets the minimum number of players required to start the arena.
     *
     * @return minimum player count
     */
    public int getMinPlayers() {
        return minPlayers;
    }

    /**
     * Sets the minimum number of players required.
     *
     * @param minPlayers minimum player count
     */
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    /**
     * Gets the maximum number of players allowed in the arena.
     *
     * @return maximum player count
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Sets the maximum number of players allowed in the arena.
     *
     * @param maxPlayers maximum player count
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Gets the list of players currently in the arena.
     *
     * @return list of players
     */
    public List<UUID> getPlayers() {
        return players;
    }

    /**
     * Adds a player to the arena.
     *
     * @param uuid player's unique id
     */
    public void addPlayer(UUID uuid) {
        players.add(uuid);
    }

    /**
     * Removes a player from the arena.
     *
     * @param uuid player's unique id
     */
    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    /**
     * Gets the teams registered in the arena.
     *
     * @return map of teams by color
     */
    public Map<TeamColor, Team> getTeams() {
        return teams;
    }

    /**
     * Gets the generators available in the arena.
     *
     * @return list of generators
     */
    public List<Generator> getGenerators() {
        return generators;
    }

    /**
     * Gets the lobby spawn location of the arena.
     *
     * @return lobby location
     */
    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    /**
     * Sets the lobby spawn location of the arena.
     *
     * @param lobbyLocation lobby location
     */
    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
    }

    /**
     * Gets the shop NPC location.
     *
     * @return location or null
     */
    public Location getShopNpcLocation() {
        return shopNpcLocation;
    }

    /**
     * Sets the shop NPC location.
     *
     * @param shopNpcLocation location
     */
    public void setShopNpcLocation(Location shopNpcLocation) {
        this.shopNpcLocation = shopNpcLocation;
    }

    /**
     * Gets the upgrade NPC location.
     *
     * @return location or null
     */
    public Location getUpgradeNpcLocation() {
        return upgradeNpcLocation;
    }

    /**
     * Sets the upgrade NPC location.
     *
     * @param upgradeNpcLocation location
     */
    public void setUpgradeNpcLocation(Location upgradeNpcLocation) {
        this.upgradeNpcLocation = upgradeNpcLocation;
    }
}
