package com.heneria.bedwars;

import com.heneria.bedwars.commands.CommandManager;
import com.heneria.bedwars.listeners.GUIListener;
import com.heneria.bedwars.listeners.ChatListener;
import com.heneria.bedwars.listeners.SetupListener;
import com.heneria.bedwars.listeners.GameListener;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.managers.SetupManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class HeneriaBedwars extends JavaPlugin {

    private static HeneriaBedwars instance;
    private ArenaManager arenaManager;
    private SetupManager setupManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("HeneriaBedwars v" + getDescription().getVersion() + " est en cours de chargement...");

        // Initialisation des managers
        this.arenaManager = new ArenaManager(this);
        this.arenaManager.loadArenas(); // Charge les arènes depuis les fichiers de config
        this.setupManager = new SetupManager();

        // Enregistrement des commandes
        CommandManager commandManager = new CommandManager(this);
        getCommand("bedwars").setExecutor(commandManager);
        getCommand("bedwars").setTabCompleter(commandManager);

        // Enregistrement des listeners (gestionnaires d'événements)
        registerListeners();


        getLogger().info("HeneriaBedwars a été activé avec succès.");
    }

    @Override
    public void onDisable() {
        getLogger().info("HeneriaBedwars a été désactivé.");
    }

    /**
     * Enregistre tous les listeners du plugin.
     */
    private void registerListeners() {
        // Gère les clics dans tous les menus personnalisés (coffres, etc.)
        getServer().getPluginManager().registerEvents(new GUIListener(), this);

        // Intercepte la saisie du chat pour la création d'arène
        getServer().getPluginManager().registerEvents(new ChatListener(), this);

        // Gère les interactions avec l'outil de positionnement
        getServer().getPluginManager().registerEvents(new SetupListener(setupManager), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
    }


    /**
     * Permet d'accéder à l'instance principale du plugin depuis n'importe où.
     * @return L'instance de HeneriaBedwars.
     */
    public static HeneriaBedwars getInstance() {
        return instance;
    }

    /**
     * Permet d'accéder au gestionnaire d'arènes.
     * @return L'instance de ArenaManager.
     */
    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public SetupManager getSetupManager() {
        return setupManager;
    }
}
