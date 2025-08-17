package com.heneria.bedwars.gui.admin.creation;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Second step of the arena creation wizard where basic settings are defined.
 */
public class ArenaSettingsMenu extends Menu {

    private final ArenaCreationWizard wizard;

    public ArenaSettingsMenu(ArenaCreationWizard wizard) {
        this.wizard = wizard;
    }

    @Override
    public String getTitle() {
        return "Paramètres de l'arène";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        ItemStack ppt = new ItemBuilder(Material.IRON_SWORD)
                .setName("Joueurs par équipe : " + wizard.getPlayersPerTeam())
                .setLore(List.of("Clique gauche: +1", "Clique droit: -1"))
                .build();
        inventory.setItem(11, ppt);

        ItemStack teams = new ItemBuilder(Material.WHITE_BANNER)
                .setName("Nombre d'équipes : " + wizard.getTeamCount())
                .setLore(List.of("Clique gauche: +1", "Clique droit: -1"))
                .build();
        inventory.setItem(15, teams);

        ItemStack confirm = new ItemBuilder(Material.EMERALD)
                .setName("Confirmer")
                .build();
        inventory.setItem(22, confirm);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == 11) {
            if (event.isLeftClick()) {
                wizard.setPlayersPerTeam(wizard.getPlayersPerTeam() + 1);
            } else if (event.isRightClick() && wizard.getPlayersPerTeam() > 1) {
                wizard.setPlayersPerTeam(wizard.getPlayersPerTeam() - 1);
            }
            setupItems();
        } else if (slot == 15) {
            if (event.isLeftClick()) {
                wizard.setTeamCount(wizard.getTeamCount() + 1);
            } else if (event.isRightClick() && wizard.getTeamCount() > 1) {
                wizard.setTeamCount(wizard.getTeamCount() - 1);
            }
            setupItems();
        } else if (slot == 22) {
            ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();
            Arena arena = new Arena(wizard.getName());
            arena.setPlayersPerTeam(wizard.getPlayersPerTeam());
            arena.setTeamCount(wizard.getTeamCount());
            arenaManager.saveArena(arena);
            arenaManager.registerArena(arena);
            HeneriaBedwars.getInstance().getCreationWizards().remove(player.getUniqueId());
            player.sendMessage("Arène " + arena.getName() + " créée !");
            player.closeInventory();
        }
    }
}
