package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.PositionTool;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Configuration menu for a specific team.
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
        return "Équipe: " + color.getDisplayName();
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        inventory.setItem(SPAWN_SLOT, new ItemBuilder(Material.COMPASS)
                .setName("Définir le Spawn")
                .build());
        inventory.setItem(BED_SLOT, new ItemBuilder(Material.RED_BED)
                .setName("Définir le Lit")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        Team team = arena.getTeams().computeIfAbsent(color, Team::new);
        int slot = event.getRawSlot();
        if (slot == SPAWN_SLOT) {
            player.closeInventory();
            PositionTool.request(player, loc -> {
                team.setSpawnLocation(loc);
                HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
                player.sendMessage("Spawn défini pour l'équipe " + color.getDisplayName());
            });
        } else if (slot == BED_SLOT) {
            player.closeInventory();
            PositionTool.request(player, loc -> {
                team.setBedLocation(loc);
                HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
                player.sendMessage("Lit défini pour l'équipe " + color.getDisplayName());
            });
        }
    }
}
