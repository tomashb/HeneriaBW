package com.heneria.bedwars.gui.shop;

import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.ShopManager;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Menu listing shop categories defined in {@code shop.yml}.
 */
public class ShopCategoryMenu extends Menu {

    private final ShopManager shopManager;
    private final Map<Integer, String> slotCategories = new HashMap<>();

    public ShopCategoryMenu(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    @Override
    public String getTitle() {
        return shopManager.getMainMenuTitle();
    }

    @Override
    public int getSize() {
        return shopManager.getMainMenuRows() * 9;
    }

    @Override
    public void setupItems() {
        for (ShopManager.MainMenuItem item : shopManager.getMainMenuItems()) {
            ItemStack stack = new ItemBuilder(item.material())
                    .setName(item.name())
                    .setLore(item.lore())
                    .build();
            inventory.setItem(item.slot(), stack);
            if (item.category() != null) {
                slotCategories.put(item.slot(), item.category());
            }
        }
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
        if (handleBack(event)) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        String categoryId = slotCategories.get(event.getRawSlot());
        if (categoryId != null) {
            ShopManager.ShopCategory category = shopManager.getCategory(categoryId);
            if (category != null) {
                String title = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', category.title()));
                player.sendMessage("§eOuverture du menu des " + title.toLowerCase() + "...");
            } else {
                player.sendMessage("§cCatégorie introuvable.");
            }
            player.closeInventory();
        }
    }
}
