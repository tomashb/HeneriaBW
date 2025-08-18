package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AdminMainMenu extends Menu {

    @Override
    public String getTitle() { return MessageManager.get("menus.admin-main-title"); }

    @Override
    public int getSize() { return 27; }

    @Override
    public void setupItems() {
        inventory.setItem(11, new ItemBuilder(Material.NETHER_STAR)
                .setName("&aCréer une Arène")
                .addLore("&7Cliquez pour lancer l'assistant")
                .addLore("&7de création d'arène.")
                .build());
        inventory.setItem(15, new ItemBuilder(Material.COMPASS)
                .setName("&eGérer les Arènes Existantes")
                .addLore("&7Cliquez pour voir et configurer")
                .addLore("&7les arènes déjà créées.")
                .build());
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
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        if (event.getSlot() == 11) {
            player.closeInventory();
            HeneriaBedwars.getInstance().getArenaManager().setPlayerInCreationMode(player);
            MessageManager.sendMessage(player, "admin.name-prompt");
            MessageManager.sendMessage(player, "admin.cancel-tips");
        } else if (event.getSlot() == 15) {
            player.closeInventory();
            new ArenaListMenu().open(player, this);
        }
    }
}
