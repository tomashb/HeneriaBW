package com.heneria.bedwars.gui.shop;

import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.managers.ResourceManager;
import com.heneria.bedwars.managers.ResourceType;
import com.heneria.bedwars.managers.ShopManager;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.GameUtils;
import com.heneria.bedwars.utils.MessageManager;
import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import org.bukkit.ChatColor;
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
 * Menu displaying all items available for a given shop category.
 */
public class ShopItemsMenu extends Menu {

    private final ShopManager shopManager;
    private final ShopManager.ShopCategory category;
    private final Map<Integer, ShopManager.ShopItem> slotItems = new HashMap<>();

    public ShopItemsMenu(ShopManager shopManager, ShopManager.ShopCategory category) {
        this.shopManager = shopManager;
        this.category = category;
    }

    @Override
    public String getTitle() {
        return ChatColor.translateAlternateColorCodes('&', category.title());
    }

    @Override
    public int getSize() {
        return category.rows() * 9;
    }

    @Override
    public void setupItems() {
        for (ShopManager.ShopItem item : category.items().values()) {
            ItemBuilder builder = new ItemBuilder(item.material())
                    .setName(item.name())
                    .addLore("&7Quantité: &f" + item.amount())
                    .addLore("&7Coût: &f" + item.costAmount() + " " + item.costResource().getDisplayName());
            ItemStack stack = builder.build();
            stack.setAmount(item.amount());
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
        ShopManager.ShopItem item = slotItems.get(event.getRawSlot());
        if (item == null) {
            return;
        }
        ResourceType type = item.costResource();
        int price = item.costAmount();
        if (ResourceManager.hasResources(player, type, price)) {
            ResourceManager.takeResources(player, type, price);
            Material material = item.material();
            Arena arena = HeneriaBedwars.getInstance().getArenaManager().getArena(player);
            Team team = arena != null ? arena.getTeam(player) : null;
            if (material.toString().endsWith("_WOOL") && team != null) {
                material = team.getColor().getWoolMaterial();
            }
            boolean isSword = material.name().endsWith("_SWORD");
            if (isSword) {
                for (int i = 0; i < player.getInventory().getSize(); i++) {
                    ItemStack invItem = player.getInventory().getItem(i);
                    if (invItem != null && invItem.getType().name().endsWith("_SWORD")) {
                        player.getInventory().setItem(i, null);
                    }
                }
            }
            ItemStack give = new ItemStack(material, item.amount());
            if (isSword) {
                ItemMeta meta = give.getItemMeta();
                if (meta != null) {
                    meta.getPersistentDataContainer().set(GameUtils.STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
                    give.setItemMeta(meta);
                }
            }
            player.getInventory().addItem(give);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        } else {
            MessageManager.sendMessage(player, "errors.not-enough-resource", "resource", type.getDisplayName().toLowerCase());
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
        }
    }
}
