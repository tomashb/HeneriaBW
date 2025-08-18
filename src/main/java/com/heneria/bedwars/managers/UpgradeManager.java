package com.heneria.bedwars.managers;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Applies upgrade effects such as enchantments and potion effects.
 */
public class UpgradeManager {

    /**
     * Adds a sharpness enchantment to the provided item.
     *
     * @param item  the item to enchant
     * @param level the level of the enchantment
     */
    public void applySharpness(ItemStack item, int level) {
        item.addUnsafeEnchantment(Enchantment.SHARPNESS, level);
    }

    /**
     * Adds a protection enchantment to the provided item.
     *
     * @param item  the item to enchant
     * @param level the level of the enchantment
     */
    public void applyProtection(ItemStack item, int level) {
        item.addUnsafeEnchantment(Enchantment.PROTECTION, level);
    }

    /**
     * Applies the haste potion effect to the player.
     *
     * @param player    the player receiving the effect
     * @param amplifier the amplifier of the effect
     * @param duration  the duration of the effect in ticks
     */
    public void applyHaste(Player player, int amplifier, int duration) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, duration, amplifier, true, true, true));
    }
}
