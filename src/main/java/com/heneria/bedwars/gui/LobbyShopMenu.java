package com.heneria.bedwars.gui;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.MessageManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Menu permettant aux joueurs d'acheter des cosmétiques dans le lobby.
 */
public class LobbyShopMenu extends Menu {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final FileConfiguration config;
    private final Map<Integer, ConfigurationSection> itemMap = new HashMap<>();

    public LobbyShopMenu() {
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "lobby_shop.yml"));
    }

    @Override
    public String getTitle() {
        return "Boutique de Cosmétiques";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public void setupItems() {
        ConfigurationSection items = config.getConfigurationSection("items");
        if (items == null) {
            return;
        }
        for (String key : items.getKeys(false)) {
            ConfigurationSection section = items.getConfigurationSection(key);
            if (section == null) continue;
            Material material = Material.matchMaterial(section.getString("display-item", "STONE"));
            if (material == null) {
                material = Material.STONE;
            }
            ItemBuilder builder = new ItemBuilder(material)
                    .setName(section.getString("name", "&b" + key));
            if (section.isList("lore")) {
                builder.setLore(section.getStringList("lore"));
            }
            ItemStack item = builder.build();
            int slot = section.getInt("slot", inventory.firstEmpty());
            inventory.setItem(slot, item);
            itemMap.put(slot, section);
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        if (handleBack(event)) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        ConfigurationSection section = itemMap.get(event.getRawSlot());
        if (section != null) {
            handlePurchase((Player) event.getWhoClicked(), section);
        }
    }

    private void handlePurchase(Player player, ConfigurationSection section) {
        Economy economy = plugin.getEconomy();
        if (economy == null) {
            MessageManager.sendMessage(player, "shop.vault-required");
            return;
        }
        double cost = section.getDouble("vault-cost", 0.0);
        if (cost > 0 && economy.getBalance(player) < cost) {
            MessageManager.sendMessage(player, "shop.not-enough-money");
            return;
        }
        if (cost > 0) {
            economy.withdrawPlayer(player, cost);
        }
        String command = section.getString("command-on-purchase", "");
        if (!command.isEmpty()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName()));
        }
        MessageManager.sendMessage(player, "shop.purchase-success");
    }
}
