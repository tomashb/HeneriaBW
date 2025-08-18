package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Lists all teams for an arena.
 */
public class TeamListMenu extends Menu {

    private final Arena arena;
    private final Map<Integer, TeamColor> teamSlots = new HashMap<>();

    public TeamListMenu(Arena arena) {
        this.arena = arena;
    }

    @Override
    public String getTitle() {
        return "Équipes : " + arena.getName();
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
                    .setName(color.getChatColor() + color.getDisplayName())
                    .addLore("&eCliquez pour configurer")
                    .build();
            inventory.setItem(slot, item);
            teamSlots.put(slot, color);
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
        if (handleBack(event)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        TeamColor color = teamSlots.get(event.getRawSlot());
        if (color != null) {
            player.closeInventory();
            new TeamConfigMenu(arena, color).open(player, this);
        }
    }
}
