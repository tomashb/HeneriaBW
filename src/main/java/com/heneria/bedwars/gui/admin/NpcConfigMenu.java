package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.setup.SetupAction;
import com.heneria.bedwars.setup.SetupType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Menu for configuring NPC positions.
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
        inventory.setItem(SHOP_SLOT, new ItemBuilder(Material.EMERALD)
                .setName("Définir le PNJ Boutique")
                .build());
        inventory.setItem(UPGRADE_SLOT, new ItemBuilder(Material.ANVIL)
                .setName("Définir le PNJ Améliorations")
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
            HeneriaBedwars.getInstance().getSetupManager().startSetup(player,
                    new SetupAction(arena, SetupType.NPC_SHOP));
        } else if (slot == UPGRADE_SLOT) {
            HeneriaBedwars.getInstance().getSetupManager().startSetup(player,
                    new SetupAction(arena, SetupType.NPC_UPGRADE));
        } else {
            return;
        }
        player.sendMessage("Clic droit pour définir la position du PNJ.");
        player.closeInventory();
    }
}
