package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles the enemy tracker compass.
 */
public class TrackerListener implements Listener {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final ArenaManager arenaManager = plugin.getArenaManager();
    private final Map<UUID, BukkitTask> tasks = new HashMap<>();
    private final int searchRadius;
    private final long updateInterval;

    public TrackerListener() {
        this.searchRadius = plugin.getConfig().getInt("tracker.search-radius", 200);
        this.updateInterval = plugin.getConfig().getLong("tracker.update-interval", 20L);
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        if (isTracker(newItem)) {
            startTracking(player);
        } else {
            stopTracking(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        stopTracking(event.getPlayer().getUniqueId());
    }

    private boolean isTracker(ItemStack item) {
        if (item == null) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String type = container.get(HeneriaBedwars.getItemTypeKey(), PersistentDataType.STRING);
        return "ENEMY_TRACKER".equals(type);
    }

    private void startTracking(Player player) {
        stopTracking(player.getUniqueId());
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !isTracker(player.getInventory().getItemInMainHand())) {
                    stopTracking(player.getUniqueId());
                    return;
                }
                Arena arena = arenaManager.getArena(player);
                if (arena == null || arena.getState() != GameState.PLAYING) {
                    return;
                }
                Team team = arena.getTeam(player);
                Player closest = null;
                double best = Double.MAX_VALUE;
                for (UUID id : arena.getAlivePlayers()) {
                    if (id.equals(player.getUniqueId())) {
                        continue;
                    }
                    Player target = Bukkit.getPlayer(id);
                    if (target == null) {
                        continue;
                    }
                    if (team != null && team.isMember(id)) {
                        continue;
                    }
                    double dist = player.getLocation().distanceSquared(target.getLocation());
                    if (dist < best && dist <= searchRadius * searchRadius) {
                        best = dist;
                        closest = target;
                    }
                }
                if (closest != null) {
                    player.setCompassTarget(closest.getLocation());
                }
            }
        }.runTaskTimer(plugin, 0L, updateInterval);
        tasks.put(player.getUniqueId(), task);
    }

    private void stopTracking(UUID uuid) {
        BukkitTask task = tasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }
}
