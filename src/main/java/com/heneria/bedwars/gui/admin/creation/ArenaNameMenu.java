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
        // --- DÉBUT DU RAPPORT MICROSCOPIQUE ---
        System.out.println("    [HENERIA DEBUG - LOUPE] Entrée dans la méthode handleClick de ArenaNameMenu.");

        event.setCancelled(true);
        System.out.println("    [HENERIA DEBUG - LOUPE] event.setCancelled(true) a été appelé.");

        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getSlot();
        System.out.println("    [HENERIA DEBUG - LOUPE] Le joueur a cliqué sur le slot: " + clickedSlot);

        if (clickedSlot != 2) {
            System.out.println("    [HENERIA DEBUG - LOUPE] Le slot n'est pas 2. Fin de la logique.");
            return;
        }
        System.out.println("    [HENERIA DEBUG - LOUPE] Le slot est bien 2. On continue.");

        ItemStack item = event.getCurrentItem();
        if (item == null) {
            System.out.println("    [HENERIA DEBUG - LOUPE] ERREUR: event.getCurrentItem() est NULL. Fin de la logique.");
            return;
        }
        System.out.println("    [HENERIA DEBUG - LOUPE] L'item n'est pas null. C'est un: " + item.getType().name());

        if (!item.hasItemMeta()) {
             System.out.println("    [HENERIA DEBUG - LOUPE] ERREUR: L'item n'a pas de métadonnées. Fin de la logique.");
             return;
        }

        String arenaName = item.getItemMeta().getDisplayName();
        System.out.println("    [HENERIA DEBUG - LOUPE] Nom récupéré: '" + arenaName + "'");
        
        // --- VALIDATION DU NOM ---
        if (arenaName.trim().isEmpty()) {
            player.sendMessage("§cLe nom de l'arène ne peut pas être vide.");
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
        System.out.println("    [HENERIA DEBUG - LOUPE] SUCCÈS: Le nom est valide. L'arène devrait être créée.");
        player.sendMessage("§aLe nom d'arène '§e" + arenaName + "§a' est valide !");
        player.sendMessage("§7La prochaine étape (configuration) sera implémentée dans un futur ticket.");
    }
}
