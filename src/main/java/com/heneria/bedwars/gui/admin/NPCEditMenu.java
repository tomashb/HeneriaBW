package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.NpcManager;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Menu allowing basic edition of a lobby NPC.
 */
public class NPCEditMenu extends Menu {

    private final NpcManager.NpcInfo info;

    public NPCEditMenu(NpcManager.NpcInfo info) {
        this.info = info;
    }

    @Override
    public String getTitle() {
        return ChatColor.YELLOW + "Éditer le PNJ";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        inventory.setItem(10, new ItemBuilder(Material.PLAYER_HEAD).setName("&eChanger le Skin").build());
        inventory.setItem(11, new ItemBuilder(Material.NAME_TAG).setName("&eChanger le Nom").build());
        inventory.setItem(12, new ItemBuilder(Material.DIAMOND_SWORD).setName("&eChanger l'Objet en Main").build());
        inventory.setItem(14, new ItemBuilder(Material.ENDER_PEARL).setName("&eSe Téléporter au PNJ").build());
        inventory.setItem(16, new ItemBuilder(Material.BARRIER).setName("&cSupprimer le PNJ").build());
        for (int i = 0; i < getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
            }
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (handleBack(event)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        if (slot == 10) {
            player.sendMessage(ChatColor.YELLOW + "Changement de skin non implémenté.");
        } else if (slot == 11) {
            player.sendMessage(ChatColor.YELLOW + "Changement de nom non implémenté.");
        } else if (slot == 12) {
            player.sendMessage(ChatColor.YELLOW + "Changement d'objet non implémenté.");
        } else if (slot == 14) {
            player.closeInventory();
            player.teleport(info.location);
        } else if (slot == 16) {
            new NPCDeleteConfirmMenu(info).open(player, this);
        }
    }
}
