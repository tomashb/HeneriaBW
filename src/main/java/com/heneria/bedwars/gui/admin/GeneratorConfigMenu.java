package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
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
 * Menu for configuring resource generators.
 */
public class GeneratorConfigMenu extends Menu {

    private final Arena arena;

    private static final int IRON_SLOT = 10;
    private static final int GOLD_SLOT = 12;
    private static final int DIAMOND_SLOT = 14;
    private static final int EMERALD_SLOT = 16;

    public GeneratorConfigMenu(Arena arena) {
        this.arena = arena;
    }

    @Override
    public String getTitle() {
        return "Générateurs";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        inventory.setItem(IRON_SLOT, new ItemBuilder(Material.IRON_INGOT)
                .setName("Ajouter un générateur de Fer")
                .build());
        inventory.setItem(GOLD_SLOT, new ItemBuilder(Material.GOLD_INGOT)
                .setName("Ajouter un générateur d'Or")
                .build());
        inventory.setItem(DIAMOND_SLOT, new ItemBuilder(Material.DIAMOND)
                .setName("Ajouter un générateur de Diamant")
                .build());
        inventory.setItem(EMERALD_SLOT, new ItemBuilder(Material.EMERALD)
                .setName("Ajouter un générateur d'Émeraude")
                .build());
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == IRON_SLOT) {
            giveTool(player, "gen_iron");
        } else if (slot == GOLD_SLOT) {
            giveTool(player, "gen_gold");
        } else if (slot == DIAMOND_SLOT) {
            giveTool(player, "gen_diamond");
        } else if (slot == EMERALD_SLOT) {
            giveTool(player, "gen_emerald");
        } else {
            return;
        }
        player.sendMessage("Clic droit pour définir la position du générateur.");
        player.closeInventory();
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
