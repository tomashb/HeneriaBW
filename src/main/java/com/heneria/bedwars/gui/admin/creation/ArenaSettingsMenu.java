package com.heneria.bedwars.gui.admin.creation;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ArenaSettingsMenu extends Menu {

    private final String arenaName;
    private int playersPerTeam = 1;
    private int teamCount = 8;

    public ArenaSettingsMenu(String arenaName) {
        this.arenaName = arenaName;
    }

    @Override
    public String getTitle() {
        return "§8Configurer: " + arenaName;
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        inventory.setItem(11, new ItemBuilder(Material.PLAYER_HEAD)
                .setName("§aJoueurs par équipe: " + playersPerTeam)
                .build());
        inventory.setItem(13, new ItemBuilder(Material.PAPER)
                .setName("§aNombre d'équipes: " + teamCount)
                .build());
        inventory.setItem(15, new ItemBuilder(Material.EMERALD_BLOCK)
                .setName("§2§lCONFIRMER LA CRÉATION")
                .build());
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
            player.sendMessage("§aL'arène '§e" + arenaName + "§a' a été créée avec succès !");
        }
    }
}
