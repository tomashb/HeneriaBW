package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.PositionTool;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Menu to configure shop NPC positions.
 */
public class NpcConfigMenu extends Menu {

    private final Arena arena;
    private static final int SHOP_SLOT = 11;
    private static final int UPGRADE_SLOT = 15;

    public NpcConfigMenu(Arena arena) {
        this.arena = arena;
    }

    @Override
    public String getTitle() {
        return "PNJ";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        inventory.setItem(SHOP_SLOT, new ItemBuilder(Material.CHEST)
                .setName("Définir PNJ Boutique")
                .build());
        inventory.setItem(UPGRADE_SLOT, new ItemBuilder(Material.ANVIL)
                .setName("Définir PNJ Améliorations")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == SHOP_SLOT) {
            player.closeInventory();
            PositionTool.request(player, loc -> {
                arena.setShopNpcLocation(loc);
                HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
                player.sendMessage("PNJ Boutique défini.");
            });
        } else if (slot == UPGRADE_SLOT) {
            player.closeInventory();
            PositionTool.request(player, loc -> {
                arena.setUpgradeNpcLocation(loc);
                HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
                player.sendMessage("PNJ Améliorations défini.");
            });
        }
    }
}
