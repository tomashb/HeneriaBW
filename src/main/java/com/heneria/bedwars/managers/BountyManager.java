package com.heneria.bedwars.managers;

import com.heneria.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Simple manager tracking kill streaks and bounties on players.
 */
public class BountyManager {

    private final Map<UUID, Integer> streaks = new HashMap<>();
    private final Map<UUID, Integer> bounties = new HashMap<>();
    private final int threshold;
    private final int reward;

    public BountyManager(int threshold, int reward) {
        this.threshold = threshold;
        this.reward = reward;
    }

    public void handleKill(Player killer, Player victim, Arena arena) {
        UUID killerId = killer.getUniqueId();
        UUID victimId = victim.getUniqueId();
        streaks.put(killerId, streaks.getOrDefault(killerId, 0) + 1);
        streaks.remove(victimId);

        if (bounties.containsKey(victimId)) {
            int amount = bounties.remove(victimId);
            killer.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, amount));
            for (UUID id : arena.getPlayers()) {
                Player p = Bukkit.getPlayer(id);
                if (p != null) {
                    p.sendMessage("§e" + killer.getName() + " a réclamé la prime sur " + victim.getName() + " !");
                }
            }
        }

        int ks = streaks.get(killerId);
        if (ks >= threshold && !bounties.containsKey(killerId)) {
            bounties.put(killerId, reward);
            for (UUID id : arena.getPlayers()) {
                Player p = Bukkit.getPlayer(id);
                if (p != null) {
                    p.sendMessage("§6" + killer.getName() + " est recherché ! Prime : " + reward + " lingots d'or.");
                }
            }
        }
    }
}
