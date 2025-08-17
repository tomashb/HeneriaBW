package com.heneria.bedwars.listeners;

import com.heneria.bedwars.utils.PositionTool;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Listener handling the use of the position tool.
 */
public class PositionToolListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // avoid double trigger
        }
        if (!PositionTool.isTool(event.getItem())) {
            return;
        }
        event.setCancelled(true);
        PositionTool.handleSelection(event.getPlayer(), event.getPlayer().getLocation());
        event.getPlayer().getInventory().remove(event.getItem());
        event.getPlayer().sendMessage("Position enregistrée.");
    }
}
