package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.managers.StatsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles loading and saving of player statistics on join/quit.
 */
public class StatsListener implements Listener {

    private final StatsManager statsManager = HeneriaBedwars.getInstance().getStatsManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        statsManager.loadPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        statsManager.savePlayer(event.getPlayer().getUniqueId());
        statsManager.unloadPlayer(event.getPlayer().getUniqueId());
    }
}

