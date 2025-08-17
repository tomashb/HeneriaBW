package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple menu listing all loaded arenas.
 */
public class ArenaListMenu extends Menu {

    private final List<Arena> arenas;
    private final int size;

    public ArenaListMenu(ArenaManager arenaManager) {
        this.arenas = new ArrayList<>(arenaManager.getAllArenas());
        this.size = Math.min(54, Math.max(9, ((arenas.size() - 1) / 9 + 1) * 9));
    }

    @Override
    public String getTitle() {
        return "Arènes";
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void setupItems() {
        for (int i = 0; i < arenas.size() && i < size; i++) {
            Arena arena = arenas.get(i);
            ItemStack item = new ItemBuilder(Material.PAPER)
                    .setName(arena.getName())
                    .build();
            inventory.setItem(i, item);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot >= 0 && slot < arenas.size()) {
            Arena arena = arenas.get(slot);
            player.sendMessage("Configuration à venir pour " + arena.getName() + "...");
            player.closeInventory();
        }
    }
}
