package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.setup.SetupAction;
import com.heneria.bedwars.setup.SetupType;
import com.heneria.bedwars.arena.elements.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Main configuration menu for a specific arena.
 */
public class ArenaConfigMenu extends Menu {

    private final Arena arena;

    private static final int SET_LOBBY_SLOT = 10;
    private static final int TEAMS_SLOT = 12;
    private static final int GENERATORS_SLOT = 14;
    private static final int NPC_SLOT = 16;
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
        inventory.setItem(SET_LOBBY_SLOT, new ItemBuilder(Material.ENDER_PEARL)
                .setName("Définir le Lobby d'attente")
                .build());

        inventory.setItem(TEAMS_SLOT, new ItemBuilder(Material.WHITE_BANNER)
                .setName("Gestion des Équipes")
                .build());

        inventory.setItem(GENERATORS_SLOT, new ItemBuilder(Material.FURNACE)
                .setName("Gestion des Générateurs")
                .build());

        inventory.setItem(NPC_SLOT, new ItemBuilder(Material.VILLAGER_SPAWN_EGG)
                .setName("Gestion des PNJ")
                .build());

        Material toggleMaterial = arena.isEnabled() ? Material.REDSTONE_BLOCK : Material.EMERALD_BLOCK;
        String toggleName = arena.isEnabled() ? "Désactiver l'arène" : "Activer l'arène";
        inventory.setItem(TOGGLE_SLOT, new ItemBuilder(toggleMaterial)
                .setName(toggleName)
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getRawSlot();
        if (slot == SET_LOBBY_SLOT) {
            HeneriaBedwars.getInstance().getSetupManager().startSetup(player,
                    new SetupAction(arena, SetupType.LOBBY));
            player.sendMessage("Faites un clic droit pour définir le lobby.");
            player.closeInventory();
        } else if (slot == TEAMS_SLOT) {
            new TeamListMenu(arena).open(player);
        } else if (slot == GENERATORS_SLOT) {
            new GeneratorConfigMenu(arena).open(player);
        } else if (slot == NPC_SLOT) {
            new NpcConfigMenu(arena).open(player);
        } else if (slot == TOGGLE_SLOT) {
            if (!arena.isEnabled()) {
                if (arena.getLobbyLocation() == null) {
                    player.sendMessage("Le lobby n'est pas défini.");
                    return;
                }
                for (Team team : arena.getTeams().values()) {
                    if (team.getSpawnLocation() == null || team.getBedLocation() == null) {
                        player.sendMessage("Toutes les équipes n'ont pas leur spawn et leur lit définis.");
                        return;
                    }
                }
                if (arena.getTeams().isEmpty()) {
                    player.sendMessage("Aucune équipe configurée.");
                    return;
                }
                arena.setEnabled(true);
                player.sendMessage("Arène activée.");
            } else {
                arena.setEnabled(false);
                player.sendMessage("Arène désactivée.");
            }
            HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
            player.closeInventory();
            new ArenaConfigMenu(arena).open(player);
        }
    }
}
