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
        player.getInventory().addItem(createStarterSword());
        player.setLevel(0);
        player.setExp(0f);
    }

    private static ItemStack createArmor(Material material, Color color) {
        ItemStack item = new ItemStack(material);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        meta.setLore(Collections.singletonList("§7Objet de départ"));
        meta.getPersistentDataContainer().set(STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
        }

    private static ItemStack createStarterSword() {
        ItemStack item = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(Collections.singletonList("§7Objet de départ"));
        meta.getPersistentDataContainer().set(STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return item;
    }
}
