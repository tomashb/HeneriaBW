package com.heneria.bedwars.gui.admin.creation;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ArenaNameMenu extends Menu {

    @Override
    public String getTitle() { return "Entrez un nom d'arène"; }
    @Override
    public int getSize() { return 0; }

    @Override
    public void setupItems() {}

    @Override
    public void open(Player player) {
        inventory = Bukkit.createInventory(this, InventoryType.ANVIL, "Nom de l'arène");
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setDisplayName("Entrez un nom");
        paper.setItemMeta(paperMeta);
        inventory.setItem(0, paper);
        player.openInventory(inventory);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        if (event.getSlot() != 2) return;
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return;

        String arenaName = item.getItemMeta().getDisplayName();

        if (arenaName.trim().isEmpty()) {
            player.sendMessage("§cLe nom de l'arène ne peut pas être vide.");
            player.closeInventory();
            return;
        }
        if (arenaName.length() > 16) {
             player.sendMessage("§cLe nom de l'arène ne peut pas dépasser 16 caractères.");
             player.closeInventory();
             return;
        }
        ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();
        if (arenaManager.getArena(arenaName) != null) {
            player.sendMessage("§cUne arène avec le nom '§e" + arenaName + "§c' existe déjà.");
            player.closeInventory();
            return;
        }

        player.closeInventory();
        player.sendMessage("§aLe nom d'arène '§e" + arenaName + "§a' est valide !");
        player.sendMessage("§7La prochaine étape (configuration) sera implémentée dans un futur ticket.");
    }
}

