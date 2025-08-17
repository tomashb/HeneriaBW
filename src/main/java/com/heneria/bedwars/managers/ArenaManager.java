package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GeneratorType;
import com.heneria.bedwars.arena.enums.TeamColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages all arenas loaded on the server.
 */
public class ArenaManager {

    private final HeneriaBedwars plugin;
    private final Map<String, Arena> arenas = new HashMap<>();

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
        File folder = new File(plugin.getDataFolder(), "arenas");
        if (!folder.exists()) {
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return;
        }

        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String name = config.getString("name");
            if (name == null) continue;
            Arena arena = new Arena(name);
            arena.setEnabled(config.getBoolean("enabled", false));
            arena.setLobbyLocation(config.getLocation("lobby"));
            arena.setShopNpcLocation(config.getLocation("npcs.shop"));
            arena.setUpgradeNpcLocation(config.getLocation("npcs.upgrade"));

            if (config.isConfigurationSection("teams")) {
                for (String key : config.getConfigurationSection("teams").getKeys(false)) {
                    TeamColor color = TeamColor.valueOf(key);
                    Team team = new Team(color);
                    team.setSpawnLocation(config.getLocation("teams." + key + ".spawn"));
                    team.setBedLocation(config.getLocation("teams." + key + ".bed"));
                    arena.getTeams().put(color, team);
                }
            }

            if (config.isList("generators")) {
                for (int i = 0; i < config.getList("generators").size(); i++) {
                    String path = "generators." + i;
                    GeneratorType type = GeneratorType.valueOf(config.getString(path + ".type"));
                    Location loc = config.getLocation(path + ".location");
                    int level = config.getInt(path + ".level", 1);
                    arena.getGenerators().add(new Generator(loc, type, level));
                }
            }

            arenas.put(name.toLowerCase(), arena);
        }
    }

    /**
     * Saves all arenas to disk.
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
        File folder = new File(plugin.getDataFolder(), "arenas");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, arena.getName().toLowerCase() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        config.set("name", arena.getName());
        config.set("enabled", arena.isEnabled());
        config.set("lobby", arena.getLobbyLocation());
        config.set("npcs.shop", arena.getShopNpcLocation());
        config.set("npcs.upgrade", arena.getUpgradeNpcLocation());

        for (Map.Entry<TeamColor, Team> entry : arena.getTeams().entrySet()) {
            String path = "teams." + entry.getKey().name();
            config.set(path + ".spawn", entry.getValue().getSpawnLocation());
            config.set(path + ".bed", entry.getValue().getBedLocation());
        }

        for (int i = 0; i < arena.getGenerators().size(); i++) {
            Generator gen = arena.getGenerators().get(i);
            String path = "generators." + i;
            config.set(path + ".type", gen.getType().name());
            config.set(path + ".location", gen.getLocation());
            config.set(path + ".level", gen.getLevel());
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save arena " + arena.getName() + ": " + e.getMessage());
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
     * Creates a new arena in memory.
     * GUI integration will handle configuration later on.
     *
     * @param name the arena name
     */
    public void createArena(String name) {
        Arena arena = new Arena(name);
        arenas.put(name.toLowerCase(), arena);
        saveArena(arena);
    }
}
