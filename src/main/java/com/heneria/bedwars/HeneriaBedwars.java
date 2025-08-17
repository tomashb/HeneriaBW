package com.heneria.bedwars;

import com.heneria.bedwars.commands.CommandManager;
import com.heneria.bedwars.listeners.GUIListener;
import com.heneria.bedwars.listeners.PositionToolListener;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for HeneriaBedwars.
 */
public final class HeneriaBedwars extends JavaPlugin {

    private static HeneriaBedwars instance;

    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("HeneriaBedwars v" + getDescription().getVersion() + " est en cours de chargement...");

        // Initialize managers
        this.arenaManager = new ArenaManager(this);
        this.arenaManager.loadArenas();

        // Register commands
        CommandManager commandManager = new CommandManager(this);
        getCommand("bedwars").setExecutor(commandManager);
        getCommand("bedwars").setTabCompleter(commandManager);

        // Register listeners
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new PositionToolListener(), this);

        getLogger().info("HeneriaBedwars a été activé avec succès.");
    }

    @Override
    public void onDisable() {
        getLogger().info("HeneriaBedwars a été désactivé.");
    }

    /**
     * Gets the singleton instance of the plugin.
     *
     * @return plugin instance
     */
    public static HeneriaBedwars getInstance() {
        return instance;
    }

    /**
     * Gets the arena manager.
     *
     * @return the arena manager
     */
    public ArenaManager getArenaManager() {
        return arenaManager;
    }
}
