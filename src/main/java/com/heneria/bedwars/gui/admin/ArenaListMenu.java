package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Lists all existing arenas for selection.
 */
public class ArenaListMenu extends Menu {

    @Override
    public String getTitle() {
        return "Sélection d'arène";
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public void setupItems() {
        int slot = 0;
        for (Arena arena : HeneriaBedwars.getInstance().getArenaManager().getAllArenas()) {
            ItemStack item = new ItemBuilder(Material.PAPER)
                    .setName(arena.getName())
                    .build();
            inventory.setItem(slot++, item);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        ItemStack item = inventory.getItem(slot);
        if (item == null) return;
        String name = item.getItemMeta().getDisplayName();
        Arena arena = HeneriaBedwars.getInstance().getArenaManager().getArena(name);
        if (arena != null) {
            player.closeInventory();
            new ArenaConfigMenu(arena).open(player);
        }
    }
}
