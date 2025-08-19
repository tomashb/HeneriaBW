package com.heneria.bedwars.utils;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.elements.Team;
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
        int armorTier = HeneriaBedwars.getInstance().getPlayerProgressionManager()
                .getArmorTier(player.getUniqueId());
        equipArmorTier(player, team, armorTier);
        player.getInventory().addItem(createStarterSword());
        player.setLevel(0);
        player.setExp(0f);
    }

    /**
     * Equips the player with armor corresponding to the provided tier.
     * Tier 0 gives full leather, higher tiers upgrade boots and leggings.
     */
    public static void equipArmorTier(Player player, Team team, int tier) {
        Color color = team != null ? team.getColor().getLeatherColor() : null;
        ItemStack helmet = createArmor(Material.LEATHER_HELMET, color);
        ItemStack chestplate = createArmor(Material.LEATHER_CHESTPLATE, color);

        Material legMat;
        Material bootMat;
        switch (tier) {
            case 1 -> {
                legMat = Material.CHAINMAIL_LEGGINGS;
                bootMat = Material.CHAINMAIL_BOOTS;
            }
            case 2 -> {
                legMat = Material.IRON_LEGGINGS;
                bootMat = Material.IRON_BOOTS;
            }
            case 3 -> {
                legMat = Material.DIAMOND_LEGGINGS;
                bootMat = Material.DIAMOND_BOOTS;
            }
            default -> {
                legMat = Material.LEATHER_LEGGINGS;
                bootMat = Material.LEATHER_BOOTS;
            }
        }

        ItemStack leggings = createArmor(legMat, legMat.name().startsWith("LEATHER") ? color : null);
        ItemStack boots = createArmor(bootMat, bootMat.name().startsWith("LEATHER") ? color : null);
        player.getInventory().setArmorContents(new ItemStack[]{boots, leggings, chestplate, helmet});
    }

    private static ItemStack createArmor(Material material, Color color) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof LeatherArmorMeta leather && color != null) {
            leather.setColor(color);
            meta = leather;
        }
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta.setLore(Collections.singletonList(MessageManager.get("items.starter-lore")));
        meta.getPersistentDataContainer().set(STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createStarterSword() {
        ItemStack item = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Collections.singletonList(MessageManager.get("items.starter-lore")));
        meta.getPersistentDataContainer().set(STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }
}
