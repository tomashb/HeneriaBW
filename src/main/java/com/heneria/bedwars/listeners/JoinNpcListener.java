package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.GameHubMenu;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

/**
 * Opens the arena selector menu when clicking a join NPC.
 */
public class JoinNpcListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (HeneriaBedwars.getInstance().getNpcManager().getMode(entity) == null) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        new GameHubMenu().open(player);
    }
}
