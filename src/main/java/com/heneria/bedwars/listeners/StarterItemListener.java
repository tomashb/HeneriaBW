package com.heneria.bedwars.listeners;

import com.heneria.bedwars.utils.GameUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

/**
 * Prevents players from dropping or moving starter kit items.
 */
public class StarterItemListener implements Listener {

    private boolean isStarterItem(ItemStack item) {
        return item != null && item.getItemMeta() != null &&
                item.getItemMeta().getPersistentDataContainer()
                        .has(GameUtils.STARTER_KEY, PersistentDataType.BYTE);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (isStarterItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        if (isStarterItem(current) || isStarterItem(cursor)) {
            if (event.getClickedInventory() != player.getInventory() || event.isShiftClick()) {
                event.setCancelled(true);
            }
        } else if (event.getHotbarButton() != -1) {
            ItemStack hotbar = player.getInventory().getItem(event.getHotbarButton());
            if (isStarterItem(hotbar) && event.getClickedInventory() != player.getInventory()) {
                event.setCancelled(true);
            }
        }
    }
}

