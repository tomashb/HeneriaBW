package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Simple bounty system placing rewards on players with killstreaks.
 */
public class BountyListener implements Listener {

    private static final int BOUNTY_THRESHOLD = 3;
    private static final int REWARD_AMOUNT = 2; // emeralds

    private final ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();
    private final Map<UUID, Integer> streaks = new HashMap<>();
    private final Set<UUID> bountied = new HashSet<>();

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        Arena arena = arenaManager.getArena(victim);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }

        streaks.remove(victim.getUniqueId());
        if (killer != null) {
            UUID killerId = killer.getUniqueId();
            int streak = streaks.getOrDefault(killerId, 0) + 1;
            streaks.put(killerId, streak);
            if (streak >= BOUNTY_THRESHOLD && !bountied.contains(killerId)) {
                bountied.add(killerId);
                Bukkit.broadcastMessage(ChatColor.GOLD + killer.getName() + " a une prime sur sa tête!");
            }
        }
        if (bountied.remove(victim.getUniqueId()) && killer != null) {
            killer.getInventory().addItem(new ItemStack(Material.EMERALD, REWARD_AMOUNT));
            Bukkit.broadcastMessage(ChatColor.GREEN + killer.getName() + " a récupéré la prime de " + victim.getName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        streaks.remove(id);
        bountied.remove(id);
    }
}
