package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.shop.ShopCategoryMenu;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Handles player interaction with shop NPCs.
 */
public class ShopListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        String type = entity.getPersistentDataContainer().get(HeneriaBedwars.getNpcKey(), PersistentDataType.STRING);
        if (!"shop".equals(type)) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        new ShopCategoryMenu(HeneriaBedwars.getInstance().getShopManager()).open(player);
    }
}
