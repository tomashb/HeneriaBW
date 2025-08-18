package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.shop.ShopCategoryMenu;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Handles player interaction with shop NPCs.
 */
public class ShopListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) {
            return;
        }
        if (!villager.getScoreboardTags().contains("shop_npc")) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        new ShopCategoryMenu(HeneriaBedwars.getInstance().getShopManager()).open(player);
    }
}
