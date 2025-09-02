package com.heneria.bedwars.managers;

import com.heneria.bedwars.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Manages kill streaks and tiered bounties on players.
 */
public class BountyManager {

    private static final String PREFIX = "§6[Prime] §r";

    private final Map<UUID, Integer> streaks = new HashMap<>();
    private final Map<UUID, Integer> bountyLevels = new HashMap<>();
    private final List<BountyTier> tiers;

    /**
     * Creates a new bounty manager with default tiers.
     */
    public BountyManager() {
        tiers = List.of(
                new BountyTier(3, Map.of(Material.GOLD_INGOT, 2, Material.DIAMOND, 1)),
                new BountyTier(5, Map.of(Material.GOLD_INGOT, 5, Material.DIAMOND, 2)),
                new BountyTier(8, Map.of(Material.GOLD_INGOT, 10, Material.EMERALD, 1))
        );
    }

    /**
     * Handles a kill and updates streaks, bounties and rewards.
     *
     * @param killer the player who made the kill
     * @param victim the player who died
     * @param arena  the arena context
     */
    public void handleKill(Player killer, Player victim, Arena arena) {
        UUID killerId = killer.getUniqueId();
        UUID victimId = victim.getUniqueId();
        streaks.put(killerId, streaks.getOrDefault(killerId, 0) + 1);
        streaks.remove(victimId);

        Integer victimBounty = bountyLevels.remove(victimId);
        if (victimBounty != null) {
            BountyTier tier = tiers.get(victimBounty);
            for (Map.Entry<Material, Integer> entry : tier.rewards().entrySet()) {
                killer.getInventory().addItem(new ItemStack(entry.getKey(), entry.getValue()));
            }
            broadcast(arena, "§e" + killer.getName() + " a réclamé la prime sur §c" + victim.getName() + "§e !");
        }

        int ks = streaks.get(killerId);
        int current = bountyLevels.getOrDefault(killerId, -1);
        for (int i = tiers.size() - 1; i >= 0; i--) {
            BountyTier tier = tiers.get(i);
            if (ks >= tier.kills() && i > current) {
                bountyLevels.put(killerId, i);
                broadcast(arena, "§c" + killer.getName() + " est recherché (Niveau " + (i + 1) + ") ! Prime: " +
                        formatRewards(tier.rewards()));
                break;
            }
        }
    }

    private void broadcast(Arena arena, String message) {
        for (UUID id : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                p.sendMessage(PREFIX + message);
            }
        }
    }

    private String formatRewards(Map<Material, Integer> rewards) {
        return rewards.entrySet().stream()
                .map(e -> e.getValue() + " " + prettyName(e.getKey()))
                .collect(Collectors.joining(", "));
    }

    private String prettyName(Material mat) {
        return switch (mat) {
            case GOLD_INGOT -> "Or";
            case DIAMOND -> "Diamant";
            case EMERALD -> "Émeraude";
            case IRON_INGOT -> "Fer";
            default -> mat.name();
        };
    }

    private record BountyTier(int kills, Map<Material, Integer> rewards) {
    }
}

