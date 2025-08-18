package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.arena.enums.GeneratorType;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
            if (config.contains("minPlayers")) {
                arena.setMinPlayers(config.getInt("minPlayers"));
            }
            if (config.contains("maxPlayers")) {
                arena.setMaxPlayers(config.getInt("maxPlayers"));
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
            if (config.contains("npc.shop.world")) {
                World w = Bukkit.getWorld(config.getString("npc.shop.world"));
                if (w != null) {
                    Location loc = new Location(w,
                            config.getDouble("npc.shop.x"),
                            config.getDouble("npc.shop.y"),
                            config.getDouble("npc.shop.z"),
                            (float) config.getDouble("npc.shop.yaw"),
                            (float) config.getDouble("npc.shop.pitch"));
                    arena.setItemShopNpcLocation(loc);
                }
            }
            if (config.contains("npc.upgrade.world")) {
                World w = Bukkit.getWorld(config.getString("npc.upgrade.world"));
                if (w != null) {
                    Location loc = new Location(w,
                            config.getDouble("npc.upgrade.x"),
                            config.getDouble("npc.upgrade.y"),
                            config.getDouble("npc.upgrade.z"),
                            (float) config.getDouble("npc.upgrade.yaw"),
                            (float) config.getDouble("npc.upgrade.pitch"));
                    arena.setUpgradeShopNpcLocation(loc);
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
        if (arena.getLobbyLocation() != null) {
            Location loc = arena.getLobbyLocation();
            config.set("lobby.world", Objects.requireNonNull(loc.getWorld()).getName());
            config.set("lobby.x", loc.getX());
            config.set("lobby.y", loc.getY());
            config.set("lobby.z", loc.getZ());
            config.set("lobby.yaw", loc.getYaw());
            config.set("lobby.pitch", loc.getPitch());
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
        if (arena.getItemShopNpcLocation() != null) {
            Location loc = arena.getItemShopNpcLocation();
            config.set("npc.shop.world", Objects.requireNonNull(loc.getWorld()).getName());
            config.set("npc.shop.x", loc.getX());
            config.set("npc.shop.y", loc.getY());
            config.set("npc.shop.z", loc.getZ());
            config.set("npc.shop.yaw", loc.getYaw());
            config.set("npc.shop.pitch", loc.getPitch());
        }
        if (arena.getUpgradeShopNpcLocation() != null) {
            Location loc = arena.getUpgradeShopNpcLocation();
            config.set("npc.upgrade.world", Objects.requireNonNull(loc.getWorld()).getName());
            config.set("npc.upgrade.x", loc.getX());
            config.set("npc.upgrade.y", loc.getY());
            config.set("npc.upgrade.z", loc.getZ());
            config.set("npc.upgrade.yaw", loc.getYaw());
            config.set("npc.upgrade.pitch", loc.getPitch());
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
    public void createArena(String name) {
        arenas.put(name.toLowerCase(), new Arena(name));
    }

    public void createAndSaveArena(String name, int playersPerTeam, int teamCount) {
        Arena arena = new Arena(name);
        arena.setMinPlayers(teamCount);
        arena.setMaxPlayers(playersPerTeam * teamCount);
        arenas.put(name.toLowerCase(), arena);
        saveArena(arena);
    }
}
