package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Menu listing all teams of an arena.
 */
public class TeamListMenu extends Menu {

    private final Arena arena;

    public TeamListMenu(Arena arena) {
        this.arena = arena;
    }

    @Override
    public String getTitle() {
        return "Équipes";
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public void setupItems() {
        int slot = 0;
        for (TeamColor color : TeamColor.values()) {
            ItemStack item = new ItemBuilder(color.getWoolMaterial())
                    .setName(color.getDisplayName())
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
        if (slot < 0 || slot >= TeamColor.values().length) return;
        TeamColor color = TeamColor.values()[slot];
        player.closeInventory();
        new TeamConfigMenu(arena, color).open(player);
    }
}
