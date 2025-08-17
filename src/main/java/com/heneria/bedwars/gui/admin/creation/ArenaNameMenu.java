package com.heneria.bedwars.gui.admin.creation;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * First step of the arena creation wizard: asks for the arena name via an anvil GUI.
 */
public class ArenaNameMenu extends Menu {

    @Override
    public String getTitle() {
        return "Nom de l'arène";
    }

    @Override
    public int getSize() {
        return 0; // Not used for anvil inventories
    }

    @Override
    public void setupItems() {
        ItemStack paper = new ItemBuilder(Material.PAPER)
                .setName("Entrez le nom")
                .build();
        inventory.setItem(0, paper);
    }

    @Override
    public void open(Player player) {
        inventory = Bukkit.createInventory(this, InventoryType.ANVIL, getTitle());
        setupItems();
        player.openInventory(inventory);
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (event.getRawSlot() == 2) {
            String name = ((AnvilInventory) event.getInventory()).getRenameText();
            if (name == null || name.isBlank()) {
                player.sendMessage("Le nom ne peut pas être vide.");
                player.closeInventory();
                return;
            }
            ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();
            if (arenaManager.getArena(name) != null) {
                player.sendMessage("Ce nom est déjà utilisé.");
                player.closeInventory();
                return;
            }
            ArenaCreationWizard wizard = new ArenaCreationWizard(name);
            HeneriaBedwars.getInstance().getCreationWizards().put(player.getUniqueId(), wizard);
            new ArenaSettingsMenu(wizard).open(player);
        }
    }
}
