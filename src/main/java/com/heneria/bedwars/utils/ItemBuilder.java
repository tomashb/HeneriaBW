package com.heneria.bedwars.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to simplify ItemStack creation.
 */
public class ItemBuilder {

    private final ItemStack itemStack;
    private final ItemMeta meta;

    /**
     * Creates a new builder for the given material.
     *
     * @param material the item material
     */
    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.meta = itemStack.getItemMeta();
    }

    /**
     * Sets the display name of the item.
     *
     * @param name the display name
     * @return this builder
     */
    public ItemBuilder setName(String name) {
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    /**
     * Sets the lore of the item.
     *
     * @param lore the lore lines
     * @return this builder
     */
    public ItemBuilder setLore(List<String> lore) {
        List<String> colored = new ArrayList<>();
        for (String line : lore) {
            colored.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(colored);
        return this;
    }

    /**
     * Adds a single line to the item's lore.
     *
     * @param line the lore line to add
     * @return this builder
     */
    public ItemBuilder addLore(String line) {
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(ChatColor.translateAlternateColorCodes('&', line));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Sets the owning player for a player head item to apply a custom skin.
     *
     * <p>This method has no effect if the item is not a player head or if the
     * provided skin name is {@code null}.</p>
     *
     * @param skin the player name whose skin should be applied
     * @return this builder
     */
    public ItemBuilder setSkullOwner(String skin) {
        if (skin != null && meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(skin));
            itemStack.setItemMeta(skullMeta);
        }
        return this;
    }

    /**
     * Builds the item stack.
     *
     * @return the resulting ItemStack
     */
    public ItemStack build() {
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
