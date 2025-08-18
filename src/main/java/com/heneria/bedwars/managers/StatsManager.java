package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.stats.PlayerStats;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages loading, caching and saving of player statistics.
 */
public class StatsManager {

    private final HeneriaBedwars plugin;
    private final DatabaseManager databaseManager;
    private final Map<UUID, PlayerStats> cache = new ConcurrentHashMap<>();

    public StatsManager(HeneriaBedwars plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;

        long minutes = plugin.getConfig().getLong("database.save-interval", 5L);
        long interval = 20L * 60L * Math.max(1L, minutes);
        new BukkitRunnable() {
            @Override
            public void run() {
                saveAll();
            }
        }.runTaskTimerAsynchronously(plugin, interval, interval);
    }

    public void loadPlayer(Player player) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerStats stats = databaseManager.loadStats(player.getUniqueId(), player.getName());
            stats.setUsername(player.getName());
            cache.put(player.getUniqueId(), stats);
        });
    }

    public void savePlayer(UUID uuid) {
        PlayerStats stats = cache.get(uuid);
        if (stats == null) return;
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> databaseManager.saveStats(stats));
    }

    public PlayerStats getStats(UUID uuid) {
        return cache.get(uuid);
    }

    public PlayerStats getStats(Player player) {
        return getStats(player.getUniqueId());
    }

    public void unloadPlayer(UUID uuid) {
        cache.remove(uuid);
    }

    public void saveAll() {
        for (PlayerStats stats : cache.values()) {
            databaseManager.saveStats(stats);
        }
    }
}

