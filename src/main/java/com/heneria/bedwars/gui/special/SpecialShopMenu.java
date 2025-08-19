package com.heneria.bedwars.gui.special;

import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.ResourceManager;
import com.heneria.bedwars.managers.ResourceType;
import com.heneria.bedwars.managers.SpecialShopManager;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.HeneriaBedwars;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;

/**
 * Inventory GUI for the special mid-game merchant.
 */
public class SpecialShopMenu extends Menu {

    private final SpecialShopManager manager;
    private final Map<Integer, SpecialShopManager.SpecialItem> slotItems = new HashMap<>();

    public SpecialShopMenu(SpecialShopManager manager) {
        this.manager = manager;
    }

    @Override
    public String getTitle() {
        return manager.getTitle();
    }

    @Override
    public int getSize() {
        return manager.getRows() * 9;
    }

    @Override
    public void setupItems() {
        for (SpecialShopManager.SpecialItem item : manager.getItems().values()) {
            ItemBuilder builder = new ItemBuilder(item.material())
                    .setName(item.name());
            for (String line : item.lore()) {
                builder.addLore(line);
            }
            builder.addLore("&7Coût: &f" + item.costAmount() + " " + item.costResource().getDisplayName());
            ItemStack stack = builder.build();
            inventory.setItem(item.slot(), stack);
            slotItems.put(item.slot(), item);
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
        SpecialShopManager.SpecialItem item = slotItems.get(event.getRawSlot());
        if (item == null) {
            return;
        }
        ResourceType type = item.costResource();
        int price = item.costAmount();
        if (ResourceManager.hasResources(player, type, price)) {
            ResourceManager.takeResources(player, type, price);
            ItemStack give = new ItemStack(item.material(), 1);
            ItemMeta meta = give.getItemMeta();
            if (meta != null && item.action() != null) {
                meta.getPersistentDataContainer().set(HeneriaBedwars.getItemTypeKey(), PersistentDataType.STRING, item.action());
                give.setItemMeta(meta);
            }
            player.getInventory().addItem(give);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        } else {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }
}

