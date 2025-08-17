package com.heneria.bedwars;

import org.bukkit.plugin.java.JavaPlugin;

public final class HeneriaBedwars extends JavaPlugin {

    private static HeneriaBedwars instance;

    // Managers à implémenter
    // private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("HeneriaBedwars v" + getDescription().getVersion() + " est en cours de chargement...");

        // 1. Charger les configurations

        // 2. Initialiser les managers
        // this.arenaManager = new ArenaManager(this);

        // 3. Enregistrer les commandes

        // 4. Enregistrer les listeners

        getLogger().info("HeneriaBedwars a été activé avec succès.");
    }

    @Override
    public void onDisable() {
        // Logique de sauvegarde ou de nettoyage
        getLogger().info("HeneriaBedwars a été désactivé.");
    }

    public static HeneriaBedwars getInstance() {
        return instance;
    }

    // Getters pour les managers
    // public ArenaManager getArenaManager() {
    //     return arenaManager;
    // }
}
