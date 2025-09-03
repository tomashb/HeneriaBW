package com.heneria.bedwars.gui;

import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Confirmation menu displayed when a player attempts to leave an arena.
 */
public class QuitArenaMenu extends Menu {

    private final Arena arena;

    public QuitArenaMenu(Arena arena) {
        this.arena = arena;
    }

    @Override
    public String getTitle() {
        return ChatColor.translateAlternateColorCodes('&', "&8» &cQuitter la partie ? &8«");
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        ItemStack light = new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(" ").build();
        ItemStack dark = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
        for (int i = 0; i < getSize(); i++) {
            inventory.setItem(i, (i % 2 == 0) ? light : dark);
        }
        inventory.setItem(12, new ItemBuilder(Material.LIME_WOOL)
                .setName("&aConfirmer")
                .addLore("&7Retourner au lobby principal.")
                .addLore("&e► Cliquez pour quitter")
                .build());
        inventory.setItem(14, new ItemBuilder(Material.RED_WOOL)
                .setName("&cAnnuler")
                .addLore("&7Rester dans la partie.")
                .addLore("&e► Cliquez pour annuler")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        int slot = event.getRawSlot();
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (slot == 12) {
            player.closeInventory();
            arena.removePlayer(player);
        } else if (slot == 14) {
            player.closeInventory();
        }
    }
}
