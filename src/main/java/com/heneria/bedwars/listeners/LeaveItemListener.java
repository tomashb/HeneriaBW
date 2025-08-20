package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.utils.GameUtils;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * Listener for the lobby leave item allowing players to exit an arena.
 */
public class LeaveItemListener implements Listener {

    public static final NamespacedKey LEAVE_ITEM_KEY = new NamespacedKey(HeneriaBedwars.getInstance(), "leave-item");
    private final ArenaManager arenaManager = HeneriaBedwars.getInstance().getArenaManager();

    /**
     * Creates the special bed item used to leave the arena lobby.
     *
     * @return the configured ItemStack
     */
    public static ItemStack createLeaveItem() {
        ItemStack item = new ItemBuilder(Material.RED_BED)
                .setName(MessageManager.get("items.leave-item-name"))
                .build();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(LEAVE_ITEM_KEY, PersistentDataType.BYTE, (byte) 1);
            meta.getPersistentDataContainer().set(GameUtils.STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        // Accept clicks in the air or on blocks
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getItem();
            if (item == null) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta == null || !meta.getPersistentDataContainer().has(LEAVE_ITEM_KEY, PersistentDataType.BYTE)) {
                return;
            }
            Player player = event.getPlayer();
            Arena arena = arenaManager.getArena(player);
            if (arena == null) {
                return;
            }
            event.setCancelled(true);
            arena.removePlayer(player);
        }
    }
}
