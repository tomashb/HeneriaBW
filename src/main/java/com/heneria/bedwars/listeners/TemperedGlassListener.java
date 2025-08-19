package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Prevents tempered glass blocks from being destroyed by explosions.
 */
public class TemperedGlassListener implements Listener {

    private final ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.blockList().isEmpty()) {
            return;
        }
        for (Arena arena : arenaManager.getAllArenas()) {
            if (arena.getWorldName() == null || event.getEntity().getWorld() == null) {
                continue;
            }
            if (!event.getEntity().getWorld().getName().equals(arena.getWorldName())) {
                continue;
            }
            event.blockList().removeIf(b -> b.getType() == Material.BLACK_STAINED_GLASS && arena.getTemperedGlassBlocks().contains(b));
        }
    }
}
