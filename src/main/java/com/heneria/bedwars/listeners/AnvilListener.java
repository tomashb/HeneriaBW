package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.admin.creation.ArenaNameMenu;
import com.heneria.bedwars.managers.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AnvilListener implements Listener {

    // Notre "tableau de post-its" pour noter les noms d'arènes en cours de frappe
    private static final Map<UUID, String> anvilInputMap = new HashMap<>();

    /**
     * Étape 1 : Le Guetteur
     * Cet événement est appelé CHAQUE FOIS que le résultat dans l'enclume est sur le point d'être mis à jour.
     */
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (event.getInventory().getHolder() instanceof ArenaNameMenu) {
            AnvilInventory anvil = event.getInventory();
            String renameText = anvil.getRenameText();

            // On met à jour le post-it avec ce que le joueur tape
            if (renameText != null && !renameText.trim().isEmpty()) {
                anvilInputMap.put(event.getView().getPlayer().getUniqueId(), renameText);
            }
        }
    }

    /**
     * Étape 2 : L'Exécuteur
     * Cet événement est appelé quand le joueur clique.
     */
    @EventHandler
    public void onAnvilClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getInventory().getHolder() instanceof ArenaNameMenu)) return;

        Player player = (Player) event.getWhoClicked();

        // On annule TOUT pour éviter le coût en XP et les bugs
        event.setCancelled(true);

        // On ne s'intéresse qu'au clic sur le slot de résultat
        if (event.getSlot() != 2) {
            return;
        }

        // On récupère le nom depuis notre "post-it", c'est 100% fiable
        String arenaName = anvilInputMap.get(player.getUniqueId());

        // On retire le post-it, le travail est fait
        anvilInputMap.remove(player.getUniqueId());
        
        // Fermer l'inventaire immédiatement
        player.closeInventory();

        // --- VALIDATION DU NOM ---
        if (arenaName == null || arenaName.trim().isEmpty()) {
            player.sendMessage("§cLe nom de l'arène ne peut pas être vide.");
            return;
        }

        if (arenaName.length() > 16) {
            player.sendMessage("§cLe nom de l'arène ne peut pas dépasser 16 caractères.");
            return;
        }

        ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();
        if (arenaManager.getArena(arenaName) != null) {
            player.sendMessage("§cUne arène avec le nom '§e" + arenaName + "§c' existe déjà.");
            return;
        }

        // --- SUCCÈS ---
        player.sendMessage("§aLe nom d'arène '§e" + arenaName + "§a' est valide !");
        player.sendMessage("§7La prochaine étape (configuration) est prête à être développée.");
        
        // NOTE: C'est ici qu'on appellera le prochain menu du wizard
        // Par exemple: new ArenaSettingsMenu(arenaName).open(player);
    }
}
