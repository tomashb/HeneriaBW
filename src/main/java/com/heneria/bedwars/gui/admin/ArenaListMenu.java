package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * GUI listing all existing arenas allowing selection for configuration.
 */
public class ArenaListMenu extends Menu {

    private final Map<Integer, Arena> arenaSlots = new HashMap<>();

    @Override
    public String getTitle() {
        return "Arènes";
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public void setupItems() {
        int slot = 0;
        for (Arena arena : HeneriaBedwars.getInstance().getArenaManager().getAllArenas()) {
            String status = arena.isEnabled() ? "&aJOUABLE" : "&cINCOMPLET";
            ItemStack item = new ItemBuilder(Material.PAPER)
                    .setName("&e" + arena.getName())
                    .addLore("&7Statut actuel: " + status)
                    .addLore("&eCliquez pour configurer")
                    .build();
            inventory.setItem(slot, item);
            arenaSlots.put(slot, arena);
            slot++;
        }
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
        for (int i = 0; i < getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Arena arena = arenaSlots.get(event.getRawSlot());
        if (arena != null) {
            player.closeInventory();
            new ArenaConfigMenu(arena).open(player);
        }
    }
}
