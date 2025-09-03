package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.gui.TeamSelectorMenu;
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
 * Listener to handle the team selector item interactions.
 */
public class TeamSelectorListener implements Listener {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final ArenaManager arenaManager = plugin.getArenaManager();
    public static final NamespacedKey TEAM_SELECTOR_KEY = new NamespacedKey(HeneriaBedwars.getInstance(), "team-selector");

    public static ItemStack createSelectorItem() {
        String skin = HeneriaBedwars.getInstance().getConfig().getString("team-selector-item.skin", "MHF_Banner");
        ItemStack item = new ItemBuilder(Material.PLAYER_HEAD)
                .setName(MessageManager.get("items.team-selector-name"))
                .setSkullTexture(skin)
                .build();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(TEAM_SELECTOR_KEY, PersistentDataType.BYTE, (byte) 1);
            meta.getPersistentDataContainer().set(GameUtils.STARTER_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        // Allow opening with any click (left or right, air or block)
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK
                && action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            item = event.getHand() == org.bukkit.inventory.EquipmentSlot.OFF_HAND
                    ? event.getPlayer().getInventory().getItemInOffHand()
                    : event.getPlayer().getInventory().getItemInMainHand();
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.getPersistentDataContainer().has(TEAM_SELECTOR_KEY, PersistentDataType.BYTE)) {
            return;
        }
        Player player = event.getPlayer();
        Arena arena = arenaManager.getArena(player);
        if (arena == null || (arena.getState() != GameState.WAITING && arena.getState() != GameState.STARTING)) {
            return;
        }
        event.setCancelled(true);
        new TeamSelectorMenu(arena).open(player);
    }
}
