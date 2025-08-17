package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.admin.creation.ArenaNameMenu;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnvilListener implements Listener {

    // Notre "tableau de post-its" pour noter les noms d'arènes en cours de frappe
    private static final Map<UUID, String> anvilInputMap = new HashMap<>();

    /**
     * Étape 1 : Le Guetteur
     * CORRECTION ICI : On utilise une méthode de lecture plus fiable.
     */
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!(event.getView().getPlayer() instanceof Player)) return;
        if (event.getInventory().getHolder() instanceof ArenaNameMenu) {
            
            // LA NOUVELLE MÉTHODE DE LECTURE - PLUS FIABLE
            ItemStack result = event.getResult();
            if (result != null && result.hasItemMeta() && result.getItemMeta().hasDisplayName()) {
                String renameText = result.getItemMeta().getDisplayName();
                
                // On met à jour le post-it avec ce que le joueur tape
                anvilInputMap.put(event.getView().getPlayer().getUniqueId(), renameText);
            }
        }
    }

    /**
     * Étape 2 : L'Exécuteur
     * (Ce code est correct et reste inchangé)
     */
    @EventHandler
    public void onAnvilClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getInventory().getHolder() instanceof ArenaNameMenu)) return;

        Player player = (Player) event.getWhoClicked();

        event.setCancelled(true);

        if (event.getSlot() != 2) {
            return;
        }

        String arenaName = anvilInputMap.get(player.getUniqueId());

        anvilInputMap.remove(player.getUniqueId());
        
        // On ne ferme l'inventaire que si on va faire une action
        // player.closeInventory();

        if (arenaName == null || arenaName.trim().isEmpty()) {
            player.closeInventory();
            player.sendMessage("§cLe nom de l'arène ne peut pas être vide.");
            return;
        }

        if (arenaName.length() > 16) {
            player.closeInventory();
            player.sendMessage("§cLe nom de l'arène ne peut pas dépasser 16 caractères.");
            return;
        }

        ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();
        if (arenaManager.getArena(arenaName) != null) {
            player.closeInventory();
            player.sendMessage("§cUne arène avec le nom '§e" + arenaName + "§c' existe déjà.");
            return;
        }

        player.closeInventory();
        player.sendMessage("§aLe nom d'arène '§e" + arenaName + "§a' est valide !");
        player.sendMessage("§7La prochaine étape (configuration) est prête à être développée.");
        
        // NOTE: C'est ici qu'on appellera le prochain menu du wizard
        // Par exemple: new ArenaSettingsMenu(arenaName).open(player);
    }
}
