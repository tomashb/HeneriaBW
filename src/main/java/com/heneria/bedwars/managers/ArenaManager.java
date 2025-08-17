package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        if (arena.getLobbyLocation() != null) {
            Location loc = arena.getLobbyLocation();
            config.set("lobby.world", Objects.requireNonNull(loc.getWorld()).getName());
            config.set("lobby.x", loc.getX());
            config.set("lobby.y", loc.getY());
            config.set("lobby.z", loc.getZ());
            config.set("lobby.yaw", loc.getYaw());
            config.set("lobby.pitch", loc.getPitch());
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
     * Creates a new arena in memory.
     * GUI integration will handle configuration later on.
     *
     * @param name the arena name
     */
    public void createArena(String name) {
        arenas.put(name.toLowerCase(), new Arena(name));
    }
}
