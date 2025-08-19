package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

/**
 * Keeps players from losing hunger during active games.
 */
public class HungerListener implements Listener {

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        Arena arena = HeneriaBedwars.getInstance().getArenaManager().getArena(player);
        if (arena != null && arena.getState() == GameState.PLAYING) {
            event.setCancelled(true);
            player.setFoodLevel(20);
        }
    }
}
