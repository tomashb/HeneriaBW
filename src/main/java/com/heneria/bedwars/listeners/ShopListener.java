package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.shop.ShopCategoryMenu;
import org.bukkit.entity.Player;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Handles player interaction with shop NPCs.
 */
public class ShopListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            return;
        }
        if (!CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked())) {
            return;
        }
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked());
        if (!"shop".equals(npc.data().get("bw-type"))) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        new ShopCategoryMenu(HeneriaBedwars.getInstance().getShopManager()).open(player);
    }
}
