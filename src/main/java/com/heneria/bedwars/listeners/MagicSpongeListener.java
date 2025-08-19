package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * Handles the magic sponge item that removes water in an area.
 */
public class MagicSpongeListener implements Listener {

    private static final int RADIUS = 3;

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        String special = meta.getPersistentDataContainer()
                .get(HeneriaBedwars.getItemTypeKey(), PersistentDataType.STRING);
        if (!"MAGIC_SPONGE".equals(special)) {
            return;
        }
        event.setCancelled(true);
        item.setAmount(item.getAmount() - 1);
        Location center = event.getBlock().getLocation();
        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int y = -RADIUS; y <= RADIUS; y++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    Block b = center.clone().add(x, y, z).getBlock();
                    if (b.getType() == Material.WATER) {
                        b.setType(Material.AIR);
                    }
                }
            }
        }
    }
}
