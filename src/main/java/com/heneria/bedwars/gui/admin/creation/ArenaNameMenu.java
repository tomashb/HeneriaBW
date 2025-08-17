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
        // Annuler l'événement pour éviter le coût en XP et le comportement par défaut
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        // On ne s'intéresse qu'au clic sur le slot de résultat (2)
        if (event.getSlot() != 2) {
            return;
        }

        // LA CORRECTION EST ICI : On regarde directement dans l'inventaire au lieu d'utiliser getCurrentItem()
        ItemStack item = event.getInventory().getItem(event.getSlot());

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        String arenaName = item.getItemMeta().getDisplayName();

        // --- VALIDATION DU NOM ---
        if (arenaName.trim().isEmpty() || arenaName.equalsIgnoreCase("Entrez un nom")) {
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

        // --- SUCCÈS ---
        player.closeInventory();
        player.sendMessage("§aLe nom d'arène '§e" + arenaName + "§a' est valide !");
        player.sendMessage("§7La prochaine étape (configuration des joueurs/équipes) est maintenant prête à être développée.");

        // NOTE: C'est ici qu'on appellera le prochain menu du wizard
        // Par exemple: new ArenaSettingsMenu(arenaName).open(player);
    }
}
