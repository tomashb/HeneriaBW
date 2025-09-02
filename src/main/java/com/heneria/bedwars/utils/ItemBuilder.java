package com.heneria.bedwars.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Bukkit;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

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
     * Applies a custom texture to a player head using either a skin URL,
     * a Base64 string or a known player name. Falls back to the skin owner
     * method if the value is not a URL or Base64 data.
     *
     * @param texture the texture data, URL or player name
     * @return this builder
     */
    public ItemBuilder setSkullTexture(String texture) {
        if (texture == null || !(meta instanceof SkullMeta skullMeta)) {
            return this;
        }
        try {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
            if (texture.startsWith("http")) {
                profile.getTextures().setSkin(new URL(texture));
            } else if (texture.length() > 60) {
                profile.getTextures().setSkin(texture);
            } else {
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(texture));
                itemStack.setItemMeta(skullMeta);
                return this;
            }
            skullMeta.setPlayerProfile(profile);
            itemStack.setItemMeta(skullMeta);
        } catch (MalformedURLException e) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(texture));
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
