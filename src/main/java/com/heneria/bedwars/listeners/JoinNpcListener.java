package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.ArenaSelectorMenu;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Opens the arena selector menu when clicking a join NPC.
 */
public class JoinNpcListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        String mode = HeneriaBedwars.getInstance().getNpcManager().getMode(entity);
        if (mode == null) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        new ArenaSelectorMenu(mode).open(player);
    }
}
