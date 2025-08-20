package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.special.SpecialShopMenu;
import com.heneria.bedwars.managers.SpecialShopManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Opens the special shop menu when interacting with the special NPC.
 */
public class SpecialNpcListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        String type = entity.getPersistentDataContainer().get(HeneriaBedwars.getNpcKey(), PersistentDataType.STRING);
        if (!"special".equals(type)) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        SpecialShopManager manager = HeneriaBedwars.getInstance().getSpecialShopManager();
        new SpecialShopMenu(manager).open(player);
    }
}

