package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.gui.PaginatedMenu;
import com.heneria.bedwars.managers.NpcManager;
import com.heneria.bedwars.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central menu for managing lobby join NPCs.
 */
public class LobbyNPCManagerMenu extends PaginatedMenu {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final Map<Integer, NpcManager.NpcInfo> npcSlots = new HashMap<>();

    @Override
    public String getTitle() {
        return ChatColor.GREEN + "Gestion des PNJ";
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    protected int getItemStartSlot() {
        return 10;
    }

    @Override
    protected int getItemsPerPage() {
        return 28;
    }

    @Override
    protected List<ItemStack> getPaginatedItems() {
        List<ItemStack> items = new ArrayList<>();
        npcSlots.clear();
        for (NpcManager.NpcInfo info : plugin.getNpcManager().getNpcs()) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(info.skin));
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', info.name));
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Mode: " + info.mode);
            meta.setLore(lore);
            head.setItemMeta(meta);
            items.add(head);
        }
        return items;
    }

    @Override
    protected void onItemSet(int index, int slot) {
        List<NpcManager.NpcInfo> list = plugin.getNpcManager().getNpcs();
        if (index < list.size()) {
            npcSlots.put(slot, list.get(index));
        }
    }

    @Override
    public void setupItems() {
        super.setupItems();
        inventory.setItem(getSize() - 5, new ItemBuilder(Material.EMERALD)
                .setName("&aCréer un PNJ")
                .addLore("&7Lancer l'assistant de création")
                .build());
        for (int i = 0; i < getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler());
            }
        }
    }

    @Override
    protected void handleMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        if (slot == getSize() - 5) {
            player.closeInventory();
            player.sendMessage(ChatColor.YELLOW + "Assistant de création de PNJ non implémenté.");
            return;
        }
        NpcManager.NpcInfo info = npcSlots.get(slot);
        if (info != null) {
            new NPCEditMenu(info).open(player, this);
        }
    }
}
