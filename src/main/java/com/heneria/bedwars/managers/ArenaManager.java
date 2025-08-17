package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

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
            folder.mkdirs();
            return;
        }
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) {
            return;
        }
        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String name = config.getString("name");
            if (name == null) {
                continue;
            }
            Arena arena = new Arena(name);
            arena.setWorldName(config.getString("world"));
            arena.setPlayersPerTeam(config.getInt("playersPerTeam", 1));
            arena.setTeamCount(config.getInt("teamCount", 2));
            arenas.put(name.toLowerCase(), arena);
        }
    }

    public void saveArena(Arena arena) {
        File folder = new File(plugin.getDataFolder(), "arenas");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, arena.getName().toLowerCase() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        config.set("name", arena.getName());
        config.set("world", arena.getWorldName());
        config.set("playersPerTeam", arena.getPlayersPerTeam());
        config.set("teamCount", arena.getTeamCount());
        config.set("lobbyLocation", null);
        config.createSection("teams");
        config.set("generators", new ArrayList<>());
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save arena " + arena.getName(), e);
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
     * Registers an arena in memory.
     *
     * @param arena the arena
     */
    public void registerArena(Arena arena) {
        arenas.put(arena.getName().toLowerCase(), arena);
    }

    public void createArena(String name) {
        arenas.put(name.toLowerCase(), new Arena(name));
    }
}
