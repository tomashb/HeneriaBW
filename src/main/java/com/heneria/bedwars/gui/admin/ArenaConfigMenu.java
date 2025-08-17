package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.PositionTool;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Main configuration menu for a specific arena.
 */
public class ArenaConfigMenu extends Menu {

    private final Arena arena;

    private static final int LOBBY_SLOT = 10;
    private static final int TEAMS_SLOT = 12;
    private static final int GENERATORS_SLOT = 14;
    private static final int NPCS_SLOT = 16;
    private static final int TOGGLE_SLOT = 22;

    public ArenaConfigMenu(Arena arena) {
        this.arena = arena;
    }

    @Override
    public String getTitle() {
        return "Config: " + arena.getName();
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        inventory.setItem(LOBBY_SLOT, new ItemBuilder(Material.ENDER_PEARL)
                .setName("Définir le Lobby d'attente")
                .build());

        inventory.setItem(TEAMS_SLOT, new ItemBuilder(Material.RED_BED)
                .setName("Gestion des Équipes")
                .build());

        inventory.setItem(GENERATORS_SLOT, new ItemBuilder(Material.IRON_INGOT)
                .setName("Gestion des Générateurs")
                .build());

        inventory.setItem(NPCS_SLOT, new ItemBuilder(Material.VILLAGER_SPAWN_EGG)
                .setName("Gestion des PNJ")
                .build());

        Material toggleMat = arena.isEnabled() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK;
        String toggleName = arena.isEnabled() ? "Désactiver l'arène" : "Activer l'arène";
        inventory.setItem(TOGGLE_SLOT, new ItemBuilder(toggleMat).setName(toggleName).build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == LOBBY_SLOT) {
            player.closeInventory();
            PositionTool.request(player, loc -> {
                arena.setLobbyLocation(loc);
                HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
                player.sendMessage("Lobby défini.");
            });
        } else if (slot == TEAMS_SLOT) {
            player.closeInventory();
            new TeamListMenu(arena).open(player);
        } else if (slot == GENERATORS_SLOT) {
            player.closeInventory();
            new GeneratorConfigMenu(arena).open(player);
        } else if (slot == NPCS_SLOT) {
            player.closeInventory();
            new NpcConfigMenu(arena).open(player);
        } else if (slot == TOGGLE_SLOT) {
            arena.setEnabled(!arena.isEnabled());
            HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
            setupItems();
            player.updateInventory();
        }
    }
}
