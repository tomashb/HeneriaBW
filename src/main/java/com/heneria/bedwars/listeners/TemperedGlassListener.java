package com.heneria.bedwars.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Prevents tempered glass blocks from being destroyed by explosions.
 */
public class TemperedGlassListener implements Listener {

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> block.getType() == Material.GLASS);
    }
}
