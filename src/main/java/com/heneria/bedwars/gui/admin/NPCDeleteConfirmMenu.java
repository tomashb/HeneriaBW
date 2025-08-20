package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.NpcManager;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Confirmation menu to delete a lobby NPC.
 */
public class NPCDeleteConfirmMenu extends Menu {

    private final NpcManager.NpcInfo info;

    public NPCDeleteConfirmMenu(NpcManager.NpcInfo info) {
        this.info = info;
    }

    @Override
    public String getTitle() {
        return ChatColor.RED + "Confirmer la suppression";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        inventory.setItem(11, new ItemBuilder(Material.RED_CONCRETE).setName("&cConfirmer").build());
        inventory.setItem(15, new ItemBuilder(Material.LIME_CONCRETE).setName("&aAnnuler").build());
        for (int i = 0; i < getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build());
            }
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        if (slot == 11) {
            player.closeInventory();
            HeneriaBedwars.getInstance().getNpcManager().removeNpc(info);
            player.sendMessage(ChatColor.GREEN + "PNJ supprimé.");
        } else if (slot == 15) {
            previousMenu.open(player, previousMenu.getPreviousMenu());
        }
    }
}
