package com.heneria.bedwars.managers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks per-player equipment tiers during a match.
 */
public class PlayerProgressionManager {

    private final Map<UUID, Integer> pickaxeTier = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> axeTier = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> armorTier = new ConcurrentHashMap<>();

    /**
     * Initializes progression data for a player.
     *
     * @param uuid player's unique identifier
     */
    public void initPlayer(UUID uuid) {
        pickaxeTier.put(uuid, 0);
        axeTier.put(uuid, 0);
        armorTier.put(uuid, 0);
    }

    /**
     * Removes all progression data for a player.
     *
     * @param uuid player's unique identifier
     */
    public void removePlayer(UUID uuid) {
        pickaxeTier.remove(uuid);
        axeTier.remove(uuid);
        armorTier.remove(uuid);
    }

    /**
     * Resets temporary progression tiers (tools) for a player while preserving
     * permanent upgrades like armor.
     *
     * @param uuid player's unique identifier
     */
    public void resetProgress(UUID uuid) {
        pickaxeTier.put(uuid, 0);
        axeTier.put(uuid, 0);
    }

    public int getPickaxeTier(UUID uuid) {
        return pickaxeTier.getOrDefault(uuid, 0);
    }

    public void setPickaxeTier(UUID uuid, int tier) {
        pickaxeTier.put(uuid, tier);
    }

    public int getAxeTier(UUID uuid) {
        return axeTier.getOrDefault(uuid, 0);
    }

    public void setAxeTier(UUID uuid, int tier) {
        axeTier.put(uuid, tier);
    }

    public int getArmorTier(UUID uuid) {
        return armorTier.getOrDefault(uuid, 0);
    }

    public void setArmorTier(UUID uuid, int tier) {
        armorTier.put(uuid, tier);
    }
}

