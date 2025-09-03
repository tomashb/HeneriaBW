package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;

/**
 * Stubbed manager: lobby NPCs no longer use animations.
 */
public class NpcAnimationManager {

    public NpcAnimationManager(HeneriaBedwars plugin, NpcManager npcManager) {
        // Animations removed, nothing to initialize
    }

    /**
     * Starts the animation task. No-op as animations are disabled.
     */
    public void start() {
        // Intentionally empty
    }

    /**
     * Stops the animation task. No-op as nothing is scheduled.
     */
    public void stop() {
        // Intentionally empty
    }
}
