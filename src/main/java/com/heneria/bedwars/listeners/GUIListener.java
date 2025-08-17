package com.heneria.bedwars.listeners;

import com.heneria.bedwars.gui.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class GUIListener implements Listener {

    // On se met en priorité HAUTE pour voir ce qu'il se passe avant les autres plugins, si jamais il y a un conflit.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        // --- DÉBUT DU RAPPORT DE DÉTECTIVE ---
        System.out.println("=============================================");
        System.out.println("[HENERIA DEBUG] Clic détecté par: " + player.getName());
        System.out.println("[HENERIA DEBUG] Type de l'inventaire: " + event.getInventory().getType().name());

        InventoryHolder holder = event.getInventory().getHolder();
        if (holder != null) {
            System.out.println("[HENERIA DEBUG] Classe du 'Holder': " + holder.getClass().getName());
        } else {
            System.out.println("[HENERIA DEBUG] Le 'Holder' est NULL !");
        }

        // Est-ce que le superviseur reconnaît notre étiquette "Menu" ?
        if (holder instanceof Menu) {
            System.out.println("[HENERIA DEBUG] SUCCÈS: Le Holder est reconnu comme une instance de 'Menu'.");
            Menu menu = (Menu) holder;
            
            // On appelle la logique spécifique du menu
            System.out.println("[HENERIA DEBUG] Appel de la méthode handleClick() de " + menu.getClass().getSimpleName());
            menu.handleClick(event);
            System.out.println("[HENERIA DEBUG] Fin de l'appel de handleClick().");

        } else {
            System.out.println("[HENERIA DEBUG] ÉCHEC: Le Holder N'EST PAS une instance de 'Menu'. Le superviseur ignore l'événement.");
        }
        System.out.println("=============================================");
        // --- FIN DU RAPPORT DE DÉTECTIVE ---
    }
}
