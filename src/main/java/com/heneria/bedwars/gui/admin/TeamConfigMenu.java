package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.setup.SetupAction;
import com.heneria.bedwars.setup.SetupType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

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
                .setName("Définir le spawn")
                .build());
        inventory.setItem(BED_SLOT, new ItemBuilder(Material.RED_BED)
                .setName("Définir le lit")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == SPAWN_SLOT) {
            HeneriaBedwars.getInstance().getSetupManager().startSetup(player,
                    new SetupAction(arena, SetupType.TEAM_SPAWN, color));
            player.sendMessage("Clic droit pour définir le spawn.");
            player.closeInventory();
        } else if (slot == BED_SLOT) {
            HeneriaBedwars.getInstance().getSetupManager().startSetup(player,
                    new SetupAction(arena, SetupType.TEAM_BED, color));
            player.sendMessage("Clic droit pour définir le lit.");
            player.closeInventory();
        }
    }
}
