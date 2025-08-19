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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Updates a compass to point to the nearest enemy when used.
 */
public class TrackerCompassListener implements Listener {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final ArenaManager arenaManager = plugin.getArenaManager();
    private final Map<UUID, BukkitTask> tasks = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String special = meta.getPersistentDataContainer()
                .get(HeneriaBedwars.getItemTypeKey(), PersistentDataType.STRING);
        if (!"TRACKER_COMPASS".equals(special)) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        BukkitTask previous = tasks.remove(player.getUniqueId());
        if (previous != null) {
            previous.cancel();
        }
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                Arena arena = arenaManager.getArena(player);
                if (arena == null || arena.getState() != GameState.PLAYING) {
                    cancel();
                    return;
                }
                Team team = arena.getTeam(player);
                Player target = null;
                double best = Double.MAX_VALUE;
                for (UUID id : arena.getPlayers()) {
                    Player p = Bukkit.getPlayer(id);
                    if (p == null || p == player) {
                        continue;
                    }
                    Team t = arena.getTeam(p);
                    if (team != null && t != null && team.equals(t)) {
                        continue;
                    }
                    double dist = p.getLocation().distanceSquared(player.getLocation());
                    if (dist < best) {
                        best = dist;
                        target = p;
                    }
                }
                if (target != null) {
                    player.setCompassTarget(target.getLocation());
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
        tasks.put(player.getUniqueId(), task);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BukkitTask task = tasks.remove(event.getPlayer().getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }
}
