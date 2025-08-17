package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.Menu;
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
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getRawSlot() != 2) {
            return;
        }

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            player.sendMessage("§cLe nom de l'arène ne peut pas être vide.");
            return;
        }

        String name = item.getItemMeta().getDisplayName().trim();
        if (name.isEmpty()) {
            player.sendMessage("§cLe nom de l'arène ne peut pas être vide.");
            return;
        }

        var manager = HeneriaBedwars.getInstance().getArenaManager();
        if (manager.getArena(name) != null) {
            player.sendMessage("§cUne arène avec le nom '" + name + "' existe déjà.");
            return;
        }

        manager.createArena(name);
        player.sendMessage("Arène " + name + " créée.");
        player.closeInventory();
        new ArenaConfigMenu(manager.getArena(name)).open(player);
    }

    @Override
    public void open(Player player) {
        inventory = Bukkit.createInventory(this, InventoryType.ANVIL, getTitle());
        setupItems();
        player.openInventory(inventory);
    }
}

