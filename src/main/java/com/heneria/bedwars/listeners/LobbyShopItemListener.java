package com.heneria.bedwars.listeners;

import com.heneria.bedwars.gui.LobbyShopMenu;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Gives players a cosmetic shop item in the lobby and handles interactions with it.
 */
public class LobbyShopItemListener implements Listener {

    private final ItemStack shopItem = new ItemBuilder(Material.CHEST)
            .setName("&aBoutique Cosmétiques")
            .addLore("&7Clique-droit pour ouvrir")
            .build();

    private boolean isShopItem(ItemStack stack) {
        if (stack == null) return false;
        return stack.isSimilar(shopItem);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.getInventory().setItem(4, shopItem); // center slot
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (isShopItem(item)) {
            event.setCancelled(true);
            new LobbyShopMenu().open(event.getPlayer());
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (isShopItem(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isShopItem(event.getCurrentItem()) || isShopItem(event.getCursor())) {
            event.setCancelled(true);
        }
    }
}
