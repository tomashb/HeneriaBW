package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks kill streaks and handles bounty rewards.
 */
public class BountyListener implements Listener {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final ArenaManager arenaManager = plugin.getArenaManager();
    private final Map<UUID, Integer> streaks = new HashMap<>();
    private final Map<UUID, Integer> bounties = new HashMap<>();
    private final int threshold;
    private final Material rewardMaterial;
    private final int rewardAmount;

    public BountyListener() {
        this.threshold = plugin.getConfig().getInt("bounty.killstreak-threshold", 5);
        this.rewardMaterial = Material.matchMaterial(plugin.getConfig().getString("bounty.reward.material", "DIAMOND"));
        this.rewardAmount = plugin.getConfig().getInt("bounty.reward.amount", 2);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Arena arena = arenaManager.getArena(victim);
        if (arena == null || arena.getState() != GameState.PLAYING) {
            return;
        }
        UUID victimId = victim.getUniqueId();
        streaks.remove(victimId);
        Integer victimBounty = bounties.remove(victimId);
        Player killer = victim.getKiller();
        if (killer == null) {
            return;
        }
        UUID killerId = killer.getUniqueId();
        int newStreak = streaks.getOrDefault(killerId, 0) + 1;
        streaks.put(killerId, newStreak);
        if (victimBounty != null) {
            killer.getInventory().addItem(new ItemStack(rewardMaterial, victimBounty));
            killer.sendMessage("§aVous avez récupéré la prime de " + victimBounty + " " + rewardMaterial.name().toLowerCase() + " !");
        }
        if (newStreak >= threshold && !bounties.containsKey(killerId)) {
            bounties.put(killerId, rewardAmount);
            for (UUID id : arena.getPlayers()) {
                Player p = Bukkit.getPlayer(id);
                if (p != null) {
                    p.sendMessage("§eUne prime a été placée sur la tête de " + killer.getName() + " !");
                }
            }
        }
    }
}
