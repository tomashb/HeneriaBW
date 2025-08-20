package com.heneria.bedwars.managers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.*;

/**
 * Simple hologram system using stacked ArmorStands.
 */
public class HologramManager {

    private final Map<String, List<ArmorStand>> holograms = new HashMap<>();

    private String key(Location loc) {
        return loc.getWorld().getName() + ':' + loc.getX() + ':' + loc.getY() + ':' + loc.getZ();
    }

    public boolean hasHologram(Location loc) {
        return holograms.containsKey(key(loc));
    }

    public void createHologram(Location loc, List<String> lines) {
        removeHologram(loc);
        List<ArmorStand> stands = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            Location lineLoc = loc.clone().add(0, -0.25 * i, 0);
            ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(lineLoc, EntityType.ARMOR_STAND);
            as.setVisible(false);
            as.setMarker(true);
            as.setGravity(false);
            as.setInvulnerable(true);
            as.setCustomName(ChatColor.translateAlternateColorCodes('&', lines.get(i)));
            as.setCustomNameVisible(true);
            stands.add(as);
        }
        holograms.put(key(loc), stands);
    }

    public void updateHologram(Location loc, List<String> lines) {
        String key = key(loc);
        List<ArmorStand> stands = holograms.get(key);
        if (stands == null) {
            createHologram(loc, lines);
            return;
        }
        int i = 0;
        for (; i < lines.size() && i < stands.size(); i++) {
            ArmorStand as = stands.get(i);
            as.setCustomName(ChatColor.translateAlternateColorCodes('&', lines.get(i)));
        }
        // Remove extra stands
        while (stands.size() > lines.size()) {
            ArmorStand as = stands.remove(stands.size() - 1);
            as.remove();
        }
        // Add missing stands
        for (; i < lines.size(); i++) {
            Location lineLoc = loc.clone().add(0, -0.25 * i, 0);
            ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(lineLoc, EntityType.ARMOR_STAND);
            as.setVisible(false);
            as.setMarker(true);
            as.setGravity(false);
            as.setInvulnerable(true);
            as.setCustomName(ChatColor.translateAlternateColorCodes('&', lines.get(i)));
            as.setCustomNameVisible(true);
            stands.add(as);
        }
    }

    public void removeHologram(Location loc) {
        List<ArmorStand> stands = holograms.remove(key(loc));
        if (stands != null) {
            for (ArmorStand as : stands) {
                as.remove();
            }
        }
    }
}
