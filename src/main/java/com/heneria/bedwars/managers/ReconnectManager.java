package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks players who disconnected mid-game and allows them to reconnect.
 */
public class ReconnectManager {

    private final HeneriaBedwars plugin;
    private final Map<UUID, Pending> pending = new ConcurrentHashMap<>();

    public ReconnectManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
    }

    public void markDisconnected(Player player, Arena arena) {
        UUID uuid = player.getUniqueId();
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                pending.remove(uuid);
            }
        }.runTaskLater(plugin, 20L * 120); // 2 minutes
        pending.put(uuid, new Pending(arena, task));
    }

    public boolean hasPending(UUID uuid) {
        return pending.containsKey(uuid);
    }

    public Arena getArena(UUID uuid) {
        Pending p = pending.get(uuid);
        return p != null ? p.arena : null;
    }

    public void reconnect(Player player) {
        UUID uuid = player.getUniqueId();
        Pending p = pending.remove(uuid);
        if (p != null) {
            p.task.cancel();
            p.arena.addPlayer(player);
        }
    }

    private record Pending(Arena arena, BukkitTask task) {}
}
