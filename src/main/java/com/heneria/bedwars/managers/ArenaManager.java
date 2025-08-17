package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;

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
     * Implementation will be added in a later step.
     */
    public void loadArenas() {
        // To be implemented in step 1.5
    }

    /**
     * Saves arenas to persistent storage.
     * Implementation will be added in a later step.
     */
    public void saveArenas() {
        // To be implemented in step 1.5
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
