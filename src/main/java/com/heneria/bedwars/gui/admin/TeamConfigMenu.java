package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Menu to configure spawn and bed for a specific team.
 */
public class TeamConfigMenu extends Menu {

    private final Arena arena;
    private final TeamColor color;

    private static final int SPAWN_SLOT = 11;
    private static final int BED_SLOT = 15;

    public TeamConfigMenu(Arena arena, TeamColor color) {
        this.arena = arena;
        this.color = color;
    }

    @Override
    public String getTitle() {
        return "Équipe " + color.getDisplayName();
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        inventory.setItem(SPAWN_SLOT, new ItemBuilder(Material.COMPASS)
                .setName("Définir le spawn")
                .build());
        inventory.setItem(BED_SLOT, new ItemBuilder(Material.RED_BED)
                .setName("Définir le lit")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == SPAWN_SLOT) {
            giveTool(player, "team_spawn_" + color.name().toLowerCase());
            player.sendMessage("Clic droit pour définir le spawn.");
            player.closeInventory();
        } else if (slot == BED_SLOT) {
            giveTool(player, "team_bed_" + color.name().toLowerCase());
            player.sendMessage("Clic droit pour définir le lit.");
            player.closeInventory();
        }
    }

    private void giveTool(Player player, String type) {
        ItemStack tool = new ItemBuilder(Material.BLAZE_ROD)
                .setName("Outil de Positionnement")
                .build();
        ItemMeta meta = tool.getItemMeta();
        NamespacedKey typeKey = new NamespacedKey(HeneriaBedwars.getInstance(), "pos-type");
        NamespacedKey arenaKey = new NamespacedKey(HeneriaBedwars.getInstance(), "arena");
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(typeKey, PersistentDataType.STRING, type);
        data.set(arenaKey, PersistentDataType.STRING, arena.getName());
        tool.setItemMeta(meta);
        player.getInventory().addItem(tool);
    }
}
