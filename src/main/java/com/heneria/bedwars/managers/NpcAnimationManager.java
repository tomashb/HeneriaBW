package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

/**
 * Runs subtle animations for lobby NPCs.
 */
public class NpcAnimationManager {

    private final HeneriaBedwars plugin;
    private final NpcManager npcManager;
    private BukkitRunnable task;
    private int tick;

    public NpcAnimationManager(HeneriaBedwars plugin, NpcManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    /**
     * Starts the breathing animation task.
     */
    public void start() {
        if (task != null) return;
        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick++;
                double offset = Math.sin(tick / 20.0) * Math.toRadians(1.5);
                NamespacedKey npcKey = HeneriaBedwars.getNpcKey();
                for (NpcManager.NpcInfo info : npcManager.getNpcs()) {
                    ArmorStand stand = findStand(info, npcKey);
                    if (stand == null) continue;
                    EulerAngle body = stand.getBodyPose();
                    stand.setBodyPose(new EulerAngle(offset, body.getY(), body.getZ()));
                }
            }
        };
        task.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Stops the animation task.
     */
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private ArmorStand findStand(NpcManager.NpcInfo info, NamespacedKey npcKey) {
        for (Entity entity : info.location.getWorld().getNearbyEntities(info.location, 1, 1, 1)) {
            if (entity instanceof ArmorStand stand) {
                String tag = stand.getPersistentDataContainer().get(npcKey, PersistentDataType.STRING);
                if (tag != null && tag.equals("JOIN_NPC:" + info.mode)) {
                    return stand;
                }
            }
        }
        return null;
    }
}
