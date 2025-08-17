package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

/**
 * Simple anvil GUI used to capture an arena name from the player.
 */
public class ArenaNameMenu extends Menu {

    @Override
    public String getTitle() {
        return "Nom de l'arène";
    }

    @Override
    public int getSize() {
        // Size is ignored for anvil inventories but required by the abstract class.
        return 3;
    }

    @Override
    public void setupItems() {
        // Slot 0 is the left input of the anvil.
        inventory.setItem(0, new ItemStack(Material.PAPER));
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        // Annuler l'événement est la première chose à faire pour éviter tout comportement par défaut.
        event.setCancelled(true);

        // Vérifier si le clic est sur le slot de résultat (slot 2)
        if (event.getSlot() != 2) {
            return; // Si ce n'est pas le slot de résultat, on ne fait rien.
        }

        // Vérifier si l'item de résultat existe
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) {
            return; // Pas d'item, on ne fait rien.
        }

        // Récupérer le nom de manière fiable
        String arenaName = item.getItemMeta().getDisplayName();

        // Valider le nom
        if (arenaName == null || arenaName.trim().isEmpty()) {
            player.sendMessage("§cLe nom de l'arène ne peut pas être vide.");
            player.closeInventory();
            return;
        }

        // Valider la non-existence de l'arène
        ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();
        if (arenaManager.getArena(arenaName) != null) {
            player.sendMessage("§cUne arène avec le nom '" + arenaName + "' existe déjà.");
            player.closeInventory();
            return;
        }

        // Toutes les vérifications ont réussi, on passe à la suite
        player.closeInventory();
        player.sendMessage("§aNom d'arène '" + arenaName + "' validé ! Configuration suivante...");

        // Déclencher l'ouverture du menu suivant (Assurez-vous que ArenaSettingsMenu existe)
        // new ArenaSettingsMenu(arenaName).open(player);
    }

    @Override
    public void open(Player player) {
        inventory = Bukkit.createInventory(this, InventoryType.ANVIL, getTitle());
        setupItems();
        player.openInventory(inventory);
    }
}

