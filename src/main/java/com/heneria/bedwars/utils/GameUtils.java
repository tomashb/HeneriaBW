package com.heneria.bedwars.utils;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.managers.PlayerProgressionManager;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import com.heneria.bedwars.utils.MessageManager;

import java.util.Collections;

/**
 * Utility methods for common game actions.
 */
public final class GameUtils {

    private GameUtils() {
    }

    /** Key used to mark starter kit items. */
    public static final NamespacedKey STARTER_KEY =
            new NamespacedKey(HeneriaBedwars.getInstance(), "starter-item");

    /**
     * Gives the default starting kit to the specified player.
     *
     * @param player the player to equip
     * @param team   the team of the player for color information
     */
    public static void giveDefaultKit(Player player, Team team) {
        player.getInventory().clear();
        if (team != null) {
            Color color = team.getColor().getLeatherColor();
        player.getInventory().setArmorContents(new ItemStack[]{
                    createArmor(Material.LEATHER_BOOTS, color),
                    createArmor(Material.LEATHER_LEGGINGS, color),
                    createArmor(Material.LEATHER_CHESTPLATE, color),
                    createArmor(Material.LEATHER_HELMET, color)
            });
        }

        PlayerProgressionManager progression = HeneriaBedwars.getInstance().getPlayerProgressionManager();
        int armorTier = progression.getArmorTier(player.getUniqueId());
        if (armorTier > 0) {
            switch (armorTier) {
                case 1 -> {
                    player.getInventory().setBoots(createBoundArmor(Material.CHAINMAIL_BOOTS));
                    player.getInventory().setLeggings(createBoundArmor(Material.CHAINMAIL_LEGGINGS));
                }
                case 2 -> {
                    player.getInventory().setBoots(createBoundArmor(Material.IRON_BOOTS));
                    player.getInventory().setLeggings(createBoundArmor(Material.IRON_LEGGINGS));
                }
                case 3 -> {
                    player.getInventory().setBoots(createBoundArmor(Material.DIAMOND_BOOTS));
                    player.getInventory().setLeggings(createBoundArmor(Material.DIAMOND_LEGGINGS));
                }
            }
        }

        player.getInventory().addItem(createStarterSword());
        player.getInventory().addItem(createStarterPickaxe());
        player.getInventory().addItem(createStarterAxe());
        player.setLevel(0);
        player.setExp(0f);
    }

    private static ItemStack createArmor(Material material, Color color) {
        ItemStack item = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta.setLore(Collections.singletonList(MessageManager.get("items.starter-lore")));
        meta.setDisplayName(MessageManager.get(getArmorNameKey(material)));
        meta.getPersistentDataContainer().set(STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createStarterSword() {
        ItemStack item = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageManager.get("items.starter-sword"));
        meta.setLore(Collections.singletonList(MessageManager.get("items.starter-lore")));
        meta.getPersistentDataContainer().set(STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createStarterPickaxe() {
        ItemStack item = new ItemStack(Material.WOODEN_PICKAXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageManager.get("items.starter-pickaxe"));
        meta.setLore(Collections.singletonList(MessageManager.get("items.starter-lore")));
        meta.getPersistentDataContainer().set(STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createStarterAxe() {
        ItemStack item = new ItemStack(Material.WOODEN_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageManager.get("items.starter-axe"));
        meta.setLore(Collections.singletonList(MessageManager.get("items.starter-lore")));
        meta.getPersistentDataContainer().set(STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createBoundArmor(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
            meta.setLore(Collections.singletonList(MessageManager.get("items.starter-lore")));
            String key = getArmorNameKey(material);
            if (key != null) {
                meta.setDisplayName(MessageManager.get(key));
            }
            meta.getPersistentDataContainer().set(STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static String getArmorNameKey(Material material) {
        return switch (material) {
            case LEATHER_HELMET, CHAINMAIL_HELMET, IRON_HELMET, DIAMOND_HELMET -> "items.starter-helmet";
            case LEATHER_CHESTPLATE, CHAINMAIL_CHESTPLATE, IRON_CHESTPLATE, DIAMOND_CHESTPLATE -> "items.starter-chestplate";
            case LEATHER_LEGGINGS, CHAINMAIL_LEGGINGS, IRON_LEGGINGS, DIAMOND_LEGGINGS -> "items.starter-leggings";
            case LEATHER_BOOTS, CHAINMAIL_BOOTS, IRON_BOOTS, DIAMOND_BOOTS -> "items.starter-boots";
            default -> null;
        };
    }

    public static void removeUpgradedToolsAndSwords(Player player) {
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;
            Material type = item.getType();
            String name = type.name();
            if ((name.endsWith("_SWORD") && type != Material.WOODEN_SWORD) ||
                (name.endsWith("_PICKAXE") && type != Material.WOODEN_PICKAXE) ||
                (name.endsWith("_AXE") && type != Material.WOODEN_AXE)) {
                player.getInventory().setItem(i, null);
            }
        }
    }
}
