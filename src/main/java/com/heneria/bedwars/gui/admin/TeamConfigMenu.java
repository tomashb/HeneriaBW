package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.MessageUtils;
import com.heneria.bedwars.setup.SetupAction;
import com.heneria.bedwars.setup.SetupType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Menu to configure spawn and bed for a specific team.
 */
public class TeamConfigMenu extends Menu {

    private final Arena arena;
    private final TeamColor color;

    private static final int SPAWN_SLOT = 11;
    private static final int BED_SLOT = 15;

    public TeamConfigMenu(Arena arena, TeamColor color) {
        this.arena = arena;
        this.color = color;
    }

    @Override
    public String getTitle() {
        return "Équipe " + color.getDisplayName();
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        inventory.setItem(SPAWN_SLOT, new ItemBuilder(Material.COMPASS)
                .setName("&eDéfinir le spawn")
                .addLore("&7Cliquez pour définir la position")
                .build());
        inventory.setItem(BED_SLOT, new ItemBuilder(Material.RED_BED)
                .setName("&eDéfinir le lit")
                .addLore("&7Cliquez pour définir la position")
                .build());
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
        int slot = event.getRawSlot();
        if (slot == SPAWN_SLOT) {
            HeneriaBedwars.getInstance().getSetupManager().startSetup(player,
                    new SetupAction(arena, SetupType.TEAM_SPAWN, color));
            MessageUtils.sendMessage(player, "&eClic droit pour définir le spawn.");
            player.closeInventory();
        } else if (slot == BED_SLOT) {
            HeneriaBedwars.getInstance().getSetupManager().startSetup(player,
                    new SetupAction(arena, SetupType.TEAM_BED, color));
            MessageUtils.sendMessage(player, "&eClic droit pour définir le lit.");
            player.closeInventory();
        }
    }
}
