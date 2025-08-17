package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Main administration menu opened via "/bedwars admin".
 */
public class AdminMainMenu extends Menu {

    private static final int CREATE_ARENA_SLOT = 11;
    private static final int MANAGE_ARENAS_SLOT = 15;

    @Override
    public String getTitle() {
        return "Gestion Bedwars";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        ItemStack createArena = new ItemBuilder(Material.NETHER_STAR)
                .setName("Créer une Arène")
                .build();
        inventory.setItem(CREATE_ARENA_SLOT, createArena);

        ItemStack manageArenas = new ItemBuilder(Material.COMPASS)
                .setName("Gérer les Arènes Existantes")
                .build();
        inventory.setItem(MANAGE_ARENAS_SLOT, manageArenas);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getRawSlot();
        if (slot == CREATE_ARENA_SLOT) {
            player.closeInventory();
            new ArenaNameMenu().open(player);
        } else if (slot == MANAGE_ARENAS_SLOT) {
            new ArenaListMenu().open(player);
        }
    }
}
