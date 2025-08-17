package com.heneria.bedwars.gui.admin.creation;

import com.heneria.bedwars.gui.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArenaNameMenu extends Menu {

    @Override
    public String getTitle() { return "Entrez un nom d'arène"; }

    @Override
    public int getSize() { return 0; } // Non utilisé

    @Override
    public void setupItems() {} // Non utilisé

    @Override
    public void open(Player player) {
        inventory = Bukkit.createInventory(this, InventoryType.ANVIL, "Nom de l'arène");
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setDisplayName("Entrez un nom");
        paper.setItemMeta(paperMeta);
        inventory.setItem(0, paper); // Slot de gauche de l'enclume
        player.openInventory(inventory);
    }

    // Cette méthode est maintenant VIDE car la logique est dans AnvilListener.java
    @Override
    public void handleClick(InventoryClickEvent event) {
        // Laisser AnvilListener gérer l'événement
    }
}
