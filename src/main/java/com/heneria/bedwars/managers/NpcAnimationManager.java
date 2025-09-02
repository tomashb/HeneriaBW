package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import org.bukkit.Location;
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
    private boolean enabled;
    private double levitationStrength;
    private double presentationSpeed;

    public NpcAnimationManager(HeneriaBedwars plugin, NpcManager npcManager) {
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    /**
     * Starts the animation task.
     */
    public void start() {
        if (task != null) return;

        enabled = plugin.getConfig().getBoolean("animations.lobby-npc.enable", true);
        if (!enabled) {
            return;
        }

        levitationStrength = plugin.getConfig().getDouble("animations.lobby-npc.levitation-strength", 0.1);
        presentationSpeed = plugin.getConfig().getDouble("animations.lobby-npc.presentation-speed", 1.0);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                tick++;
                double phase = tick / 20.0 * presentationSpeed;
                double bodyOffset = Math.sin(phase) * Math.toRadians(1.5);
                double armOffset = Math.sin(phase) * Math.toRadians(20);
                double yOffset = Math.sin(phase) * levitationStrength;
                NamespacedKey npcKey = HeneriaBedwars.getNpcKey();
                for (NpcManager.NpcInfo info : npcManager.getNpcs()) {
                    ArmorStand stand = findStand(info, npcKey);
                    if (stand == null) continue;
                    EulerAngle body = stand.getBodyPose();
                    stand.setBodyPose(new EulerAngle(bodyOffset, body.getY(), body.getZ()));

                    stand.setRightArmPose(new EulerAngle(Math.toRadians(-10) + armOffset, 0, 0));

                    Location base = info.location.clone();
                    base.add(0, yOffset, 0);
                    stand.teleport(base);
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
                if (tag != null && tag.equals("JOIN_NPC:" + info.id)) {
                    return stand;
                }
            }
        }
        return null;
    }
}
