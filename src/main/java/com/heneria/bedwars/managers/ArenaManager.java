package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.arena.enums.GeneratorType;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Manages all arenas loaded on the server.
 */
public class ArenaManager {

    private final HeneriaBedwars plugin;
    private final Map<String, Arena> arenas = new HashMap<>();
    private final Set<UUID> playersInCreationMode = new HashSet<>();

    /**
     * Creates a new arena manager.
     *
     * @param plugin the plugin instance
     */
    public ArenaManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads arenas from persistent storage.
     */
    public void loadArenas() {
        arenas.clear();
        File dir = new File(plugin.getDataFolder(), "arenas");
        if (!dir.exists()) {
            return;
        }
        for (File file : Objects.requireNonNull(dir.listFiles((d, name) -> name.endsWith(".yml")))) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String name = file.getName().substring(0, file.getName().length() - 4);
            Arena arena = new Arena(name);
            if (config.contains("enabled")) {
                arena.setEnabled(config.getBoolean("enabled"));
            }
            if (config.contains("world")) {
                arena.setWorldName(config.getString("world"));
                World arenaWorld = Bukkit.getWorld(arena.getWorldName());
                if (arenaWorld != null) {
                    arenaWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                }
            }
            if (config.contains("minPlayers")) {
                arena.setMinPlayers(config.getInt("minPlayers"));
            }
            if (config.contains("maxPlayers")) {
                arena.setMaxPlayers(config.getInt("maxPlayers"));
            }
            if (config.contains("boundaries.max-y")) {
                arena.setMaxBuildY(config.getInt("boundaries.max-y"));
            }
            if (config.contains("boundaries.max-distance-from-center")) {
                arena.setMaxBuildDistance(config.getInt("boundaries.max-distance-from-center"));
            }
            if (config.contains("lobby.world")) {
                World world = Bukkit.getWorld(config.getString("lobby.world"));
                if (world != null) {
                    Location loc = new Location(world,
                            config.getDouble("lobby.x"),
                            config.getDouble("lobby.y"),
                            config.getDouble("lobby.z"),
                            (float) config.getDouble("lobby.yaw"),
                            (float) config.getDouble("lobby.pitch"));
                    arena.setLobbyLocation(loc);
                }
            }
            if (config.contains("special-npc.world")) {
                World w = Bukkit.getWorld(config.getString("special-npc.world"));
                if (w != null) {
                    Location loc = new Location(w,
                            config.getDouble("special-npc.x"),
                            config.getDouble("special-npc.y"),
                            config.getDouble("special-npc.z"),
                            (float) config.getDouble("special-npc.yaw"),
                            (float) config.getDouble("special-npc.pitch"));
                    arena.setSpecialNpcLocation(loc);
                }
            }
            if (config.contains("teams")) {
                for (String key : Objects.requireNonNull(config.getConfigurationSection("teams")).getKeys(false)) {
                    TeamColor color = TeamColor.valueOf(key.toUpperCase());
                    Team team = new Team(color);
                    if (config.contains("teams." + key + ".spawn.world")) {
                        World w = Bukkit.getWorld(config.getString("teams." + key + ".spawn.world"));
                        if (w != null) {
                            Location loc = new Location(w,
                                    config.getDouble("teams." + key + ".spawn.x"),
                                    config.getDouble("teams." + key + ".spawn.y"),
                                    config.getDouble("teams." + key + ".spawn.z"),
                                    (float) config.getDouble("teams." + key + ".spawn.yaw"),
                                    (float) config.getDouble("teams." + key + ".spawn.pitch"));
                            team.setSpawnLocation(loc);
                        }
                    }
                    if (config.contains("teams." + key + ".bed.world")) {
                        World w = Bukkit.getWorld(config.getString("teams." + key + ".bed.world"));
                        if (w != null) {
                            Location loc = new Location(w,
                                    config.getDouble("teams." + key + ".bed.x"),
                                    config.getDouble("teams." + key + ".bed.y"),
                                    config.getDouble("teams." + key + ".bed.z"),
                                    (float) config.getDouble("teams." + key + ".bed.yaw"),
                                    (float) config.getDouble("teams." + key + ".bed.pitch"));
                            team.setBedLocation(loc);
                        }
                    }
                    if (config.contains("teams." + key + ".npc.shop.world")) {
                        World w = Bukkit.getWorld(config.getString("teams." + key + ".npc.shop.world"));
                        if (w != null) {
                            Location loc = new Location(w,
                                    config.getDouble("teams." + key + ".npc.shop.x"),
                                    config.getDouble("teams." + key + ".npc.shop.y"),
                                    config.getDouble("teams." + key + ".npc.shop.z"),
                                    (float) config.getDouble("teams." + key + ".npc.shop.yaw"),
                                    (float) config.getDouble("teams." + key + ".npc.shop.pitch"));
                            team.setItemShopNpcLocation(loc);
                        }
                    }
                    if (config.contains("teams." + key + ".npc.shop.chestplate")) {
                        team.setItemShopChestplate(Material.matchMaterial(config.getString("teams." + key + ".npc.shop.chestplate")));
                    }
                    if (config.contains("teams." + key + ".npc.shop.leggings")) {
                        team.setItemShopLeggings(Material.matchMaterial(config.getString("teams." + key + ".npc.shop.leggings")));
                    }
                    if (config.contains("teams." + key + ".npc.shop.boots")) {
                        team.setItemShopBoots(Material.matchMaterial(config.getString("teams." + key + ".npc.shop.boots")));
                    }
                    if (config.contains("teams." + key + ".npc.upgrade.world")) {
                        World w = Bukkit.getWorld(config.getString("teams." + key + ".npc.upgrade.world"));
                        if (w != null) {
                            Location loc = new Location(w,
                                    config.getDouble("teams." + key + ".npc.upgrade.x"),
                                    config.getDouble("teams." + key + ".npc.upgrade.y"),
                                    config.getDouble("teams." + key + ".npc.upgrade.z"),
                                    (float) config.getDouble("teams." + key + ".npc.upgrade.yaw"),
                                    (float) config.getDouble("teams." + key + ".npc.upgrade.pitch"));
                            team.setUpgradeShopNpcLocation(loc);
                        }
                    }
                    if (config.contains("teams." + key + ".npc.upgrade.chestplate")) {
                        team.setUpgradeShopChestplate(Material.matchMaterial(config.getString("teams." + key + ".npc.upgrade.chestplate")));
                    }
                    if (config.contains("teams." + key + ".npc.upgrade.leggings")) {
                        team.setUpgradeShopLeggings(Material.matchMaterial(config.getString("teams." + key + ".npc.upgrade.leggings")));
                    }
                    if (config.contains("teams." + key + ".npc.upgrade.boots")) {
                        team.setUpgradeShopBoots(Material.matchMaterial(config.getString("teams." + key + ".npc.upgrade.boots")));
                    }
                    arena.getTeams().put(color, team);
                }
            }
            if (config.contains("generators")) {
                for (String key : Objects.requireNonNull(config.getConfigurationSection("generators")).getKeys(false)) {
                    String base = "generators." + key + ".";
                    World w = Bukkit.getWorld(config.getString(base + "world"));
                    GeneratorType type = GeneratorType.valueOf(config.getString(base + "type"));
                    if (w != null && type != null) {
                        Location loc = new Location(w,
                                config.getDouble(base + "x"),
                                config.getDouble(base + "y"),
                                config.getDouble(base + "z"));
                        int level = config.getInt(base + "level", 1);
                        arena.getGenerators().add(new Generator(loc, type, level));
                    }
                }
            }
            arenas.put(name.toLowerCase(), arena);
        }
    }

    /**
     * Saves all arenas to persistent storage.
     */
    public void saveArenas() {
        for (Arena arena : arenas.values()) {
            saveArena(arena);
        }
    }

    /**
     * Saves a single arena to its YAML file.
     *
     * @param arena arena to save
     */
    public void saveArena(Arena arena) {
        File dir = new File(plugin.getDataFolder(), "arenas");
        if (!dir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        File file = new File(dir, arena.getName().toLowerCase() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        config.set("enabled", arena.isEnabled());
        config.set("minPlayers", arena.getMinPlayers());
        config.set("maxPlayers", arena.getMaxPlayers());
        config.set("boundaries.max-y", arena.getMaxBuildY());
        config.set("boundaries.max-distance-from-center", arena.getMaxBuildDistance());
        if (arena.getWorldName() != null) {
            config.set("world", arena.getWorldName());
        }
        if (arena.getLobbyLocation() != null) {
            Location loc = arena.getLobbyLocation();
            config.set("lobby.world", Objects.requireNonNull(loc.getWorld()).getName());
            config.set("lobby.x", loc.getX());
            config.set("lobby.y", loc.getY());
            config.set("lobby.z", loc.getZ());
            config.set("lobby.yaw", loc.getYaw());
            config.set("lobby.pitch", loc.getPitch());
        }
        if (arena.getSpecialNpcLocation() != null) {
            Location loc = arena.getSpecialNpcLocation();
            config.set("special-npc.world", Objects.requireNonNull(loc.getWorld()).getName());
            config.set("special-npc.x", loc.getX());
            config.set("special-npc.y", loc.getY());
            config.set("special-npc.z", loc.getZ());
            config.set("special-npc.yaw", loc.getYaw());
            config.set("special-npc.pitch", loc.getPitch());
        }
        if (!arena.getTeams().isEmpty()) {
            for (Map.Entry<TeamColor, Team> entry : arena.getTeams().entrySet()) {
                String base = "teams." + entry.getKey().name().toLowerCase() + ".";
                Team team = entry.getValue();
                if (team.getSpawnLocation() != null) {
                    Location loc = team.getSpawnLocation();
                    config.set(base + "spawn.world", Objects.requireNonNull(loc.getWorld()).getName());
                    config.set(base + "spawn.x", loc.getX());
                    config.set(base + "spawn.y", loc.getY());
                    config.set(base + "spawn.z", loc.getZ());
                    config.set(base + "spawn.yaw", loc.getYaw());
                    config.set(base + "spawn.pitch", loc.getPitch());
                }
                if (team.getBedLocation() != null) {
                    Location loc = team.getBedLocation();
                    config.set(base + "bed.world", Objects.requireNonNull(loc.getWorld()).getName());
                    config.set(base + "bed.x", loc.getX());
                    config.set(base + "bed.y", loc.getY());
                    config.set(base + "bed.z", loc.getZ());
                    config.set(base + "bed.yaw", loc.getYaw());
                    config.set(base + "bed.pitch", loc.getPitch());
                }
                if (team.getItemShopNpcLocation() != null) {
                    Location loc = team.getItemShopNpcLocation();
                    config.set(base + "npc.shop.world", Objects.requireNonNull(loc.getWorld()).getName());
                    config.set(base + "npc.shop.x", loc.getX());
                    config.set(base + "npc.shop.y", loc.getY());
                    config.set(base + "npc.shop.z", loc.getZ());
                    config.set(base + "npc.shop.yaw", loc.getYaw());
                    config.set(base + "npc.shop.pitch", loc.getPitch());
                }
                if (team.getItemShopChestplate() != null) {
                    config.set(base + "npc.shop.chestplate", team.getItemShopChestplate().name());
                }
                if (team.getItemShopLeggings() != null) {
                    config.set(base + "npc.shop.leggings", team.getItemShopLeggings().name());
                }
                if (team.getItemShopBoots() != null) {
                    config.set(base + "npc.shop.boots", team.getItemShopBoots().name());
                }
                if (team.getUpgradeShopNpcLocation() != null) {
                    Location loc = team.getUpgradeShopNpcLocation();
                    config.set(base + "npc.upgrade.world", Objects.requireNonNull(loc.getWorld()).getName());
                    config.set(base + "npc.upgrade.x", loc.getX());
                    config.set(base + "npc.upgrade.y", loc.getY());
                    config.set(base + "npc.upgrade.z", loc.getZ());
                    config.set(base + "npc.upgrade.yaw", loc.getYaw());
                    config.set(base + "npc.upgrade.pitch", loc.getPitch());
                }
                if (team.getUpgradeShopChestplate() != null) {
                    config.set(base + "npc.upgrade.chestplate", team.getUpgradeShopChestplate().name());
                }
                if (team.getUpgradeShopLeggings() != null) {
                    config.set(base + "npc.upgrade.leggings", team.getUpgradeShopLeggings().name());
                }
                if (team.getUpgradeShopBoots() != null) {
                    config.set(base + "npc.upgrade.boots", team.getUpgradeShopBoots().name());
                }
            }
        }
        if (!arena.getGenerators().isEmpty()) {
            int i = 0;
            for (Generator gen : arena.getGenerators()) {
                String base = "generators." + i + ".";
                Location loc = gen.getLocation();
                config.set(base + "world", Objects.requireNonNull(loc.getWorld()).getName());
                config.set(base + "x", loc.getX());
                config.set(base + "y", loc.getY());
                config.set(base + "z", loc.getZ());
                config.set(base + "type", gen.getType().name());
                config.set(base + "level", gen.getLevel());
                i++;
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Impossible de sauvegarder l'arène " + arena.getName());
        }
    }

    /**
     * Retrieves an arena by name.
     *
     * @param name the arena name
     * @return the arena or {@code null} if not found
     */
    public Arena getArena(String name) {
        return arenas.get(name.toLowerCase());
    }

    /**
     * Gets all loaded arenas.
     *
     * @return a collection of arenas
     */
    public Collection<Arena> getAllArenas() {
        return arenas.values();
    }

    /**
     * Deletes an arena from memory and disk.
     *
     * @param name the arena name
     * @return {@code true} if the arena existed and was removed
     */
    public boolean deleteArena(String name) {
        Arena removed = arenas.remove(name.toLowerCase());
        if (removed == null) {
            return false;
        }
        File dir = new File(plugin.getDataFolder(), "arenas");
        File file = new File(dir, name.toLowerCase() + ".yml");
        if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
        return true;
    }

    /**
     * Retrieves the arena a player is currently in.
     *
     * @param uuid the player's unique id
     * @return the arena or {@code null} if none
     */
    public Arena getArenaByPlayer(UUID uuid) {
        for (Arena arena : arenas.values()) {
            if (arena.getPlayers().contains(uuid)) {
                return arena;
            }
        }
        return null;
    }

    public Arena getArena(Player player) {
        return getArenaByPlayer(player.getUniqueId());
    }

    public void setPlayerInCreationMode(Player player) {
        playersInCreationMode.add(player.getUniqueId());
    }

    public void removePlayerFromCreationMode(Player player) {
        playersInCreationMode.remove(player.getUniqueId());
    }

    public boolean isPlayerInCreationMode(Player player) {
        return playersInCreationMode.contains(player.getUniqueId());
    }

    /**
     * Creates a new arena in memory.
     * GUI integration will handle configuration later on.
     *
     * @param name the arena name
     */
    public void createArena(String name, String worldName) {
        Arena arena = new Arena(name);
        arena.setWorldName(worldName);
        arenas.put(name.toLowerCase(), arena);
    }

    public void createArena(String name) {
        createArena(name, null);
    }

    /**
     * Creates a new arena with the given parameters and saves it to disk.
     *
     * @param name           the arena name
     * @param playersPerTeam number of players per team
     * @param teamCount      number of teams
     * @param worldName      name of the world where the arena resides
     */
    public void createAndSaveArena(String name, int playersPerTeam, int teamCount, String worldName) {
        Arena arena = new Arena(name);
        arena.setWorldName(worldName);
        arena.setMinPlayers(teamCount);
        arena.setMaxPlayers(playersPerTeam * teamCount);
        arenas.put(name.toLowerCase(), arena);
        saveArena(arena);
    }

    public void createAndSaveArena(String name, int playersPerTeam, int teamCount) {
        createAndSaveArena(name, playersPerTeam, teamCount, null);
    }
}
