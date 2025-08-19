package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.special.SpecialShopMenu;
import com.heneria.bedwars.managers.SpecialShopManager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Opens the special shop menu when interacting with the special NPC.
 */
public class SpecialNpcListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Villager villager)) {
            return;
        }
        if (!villager.getScoreboardTags().contains("special_npc")) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        SpecialShopManager manager = HeneriaBedwars.getInstance().getSpecialShopManager();
        new SpecialShopMenu(manager).open(player);
    }
}

