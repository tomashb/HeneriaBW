package com.heneria.bedwars.gui.admin.creation;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ArenaSettingsMenu extends Menu {

    private final String arenaName;
    private int playersPerTeam = 1;
    private int teamCount = 8;

    public ArenaSettingsMenu(String arenaName) {
        this.arenaName = arenaName;
    }

    @Override
    public String getTitle() {
        return MessageManager.get("menus.arena-settings-title", "arena", arenaName);
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        inventory.setItem(11, new ItemBuilder(Material.PLAYER_HEAD)
                .setName("&aJoueurs par équipe: " + playersPerTeam)
                .addLore("&7Clic gauche: &e+1")
                .addLore("&7Clic droit: &e-1")
                .build());
        inventory.setItem(13, new ItemBuilder(Material.PAPER)
                .setName("&aNombre d'équipes: " + teamCount)
                .addLore("&7Clic gauche: &e+1")
                .addLore("&7Clic droit: &e-1")
                .build());
        inventory.setItem(15, new ItemBuilder(Material.EMERALD_BLOCK)
                .setName("&2&lCONFIRMER LA CRÉATION")
                .addLore("&7Valider et créer l'arène")
                .build());
        ItemStack filler = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(" ").build();
        for (int i = 0; i < getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        int slot = event.getSlot();
        if (slot == 11) {
            if (event.isLeftClick()) {
                playersPerTeam++;
            } else if (event.isRightClick() && playersPerTeam > 1) {
                playersPerTeam--;
            }
            setupItems();
        } else if (slot == 13) {
            if (event.isLeftClick()) {
                teamCount++;
            } else if (event.isRightClick() && teamCount > 1) {
                teamCount--;
            }
            setupItems();
        } else if (slot == 15) {
            Player player = (Player) event.getWhoClicked();
            player.closeInventory();
            HeneriaBedwars.getInstance().getArenaManager().createAndSaveArena(arenaName, playersPerTeam, teamCount);
            MessageManager.sendMessage(player, "admin.arena-created", "arena", arenaName);
        }
    }
}
