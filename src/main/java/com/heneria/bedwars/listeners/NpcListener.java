package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.gui.GameHubMenu;
import com.heneria.bedwars.gui.shop.ShopMenu;
import com.heneria.bedwars.gui.upgrades.TeamUpgradeCategoryMenu;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Handles interactions with shop and upgrade NPCs using PDC tags.
 */
public class NpcListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        String tag = entity.getPersistentDataContainer().get(HeneriaBedwars.getNpcKey(), PersistentDataType.STRING);
        System.out.println("[DEBUG-NPC] Interaction détectée. Tag : " + tag);
        if (tag == null) {
            System.out.println("[DEBUG-NPC] Aucun tag trouvé. Interaction ignorée.");
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (tag.startsWith("JOIN_NPC")) {
            System.out.println("[DEBUG-NPC] Clic sur PNJ. Tag trouvé : " + tag + ". Ouverture du hub.");
            new GameHubMenu().open(player);
        } else if (tag.startsWith("ITEM_SHOP")) {
            System.out.println("[DEBUG-NPC] Clic sur PNJ. Tag trouvé : " + tag + ". Ouverture du menu boutique.");
            HeneriaBedwars plugin = HeneriaBedwars.getInstance();
            new ShopMenu(plugin.getShopManager(), plugin.getPlayerProgressionManager(), player).open(player);
        } else if (tag.startsWith("UPGRADE_SHOP")) {
            System.out.println("[DEBUG-NPC] Clic sur PNJ. Tag trouvé : " + tag + ". Ouverture du menu améliorations.");
            HeneriaBedwars plugin = HeneriaBedwars.getInstance();
            Arena arena = plugin.getArenaManager().getArenaByPlayer(player.getUniqueId());
            if (arena == null) {
                System.out.println("[DEBUG-NPC] Joueur sans arène. Interaction ignorée.");
                return;
            }
            Team team = arena.getTeam(player);
            if (team == null) {
                System.out.println("[DEBUG-NPC] Joueur sans équipe. Interaction annulée.");
                MessageManager.sendMessage(player, "errors.no-team");
                return;
            }
            new TeamUpgradeCategoryMenu(plugin, arena, team).open(player);
        } else {
            System.out.println("[DEBUG-NPC] Tag inconnu : " + tag + ". Interaction ignorée.");
        }
    }
}
