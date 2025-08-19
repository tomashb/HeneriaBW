package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages persistent join NPCs for the main lobby.
 */
public class NpcManager {

    private final HeneriaBedwars plugin;
    private final File file;
    private final YamlConfiguration config;
    private final List<NpcInfo> npcs = new ArrayList<>();

    public NpcManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "npcs.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        loadNpcs();
    }

    private double getDouble(Map<?, ?> map, String key) {
        Object obj = map.get(key);
        if (obj instanceof Number n) {
            return n.doubleValue();
        }
        return 0;
    }

    public void loadNpcs() {
        npcs.clear();
        List<Map<?, ?>> list = config.getMapList("npcs");
        for (Map<?, ?> map : list) {
            String world = (String) map.get("world");
            if (world == null) continue;
            Location loc = new Location(
                    Bukkit.getWorld(world),
                    getDouble(map, "x"),
                    getDouble(map, "y"),
                    getDouble(map, "z"),
                    (float) getDouble(map, "yaw"),
                    (float) getDouble(map, "pitch"));
            String mode = (String) map.get("mode");
            NpcInfo info = new NpcInfo(loc, mode);
            spawnNpc(info);
            npcs.add(info);
        }
    }

    public void spawnNpc(NpcInfo info) {
        if (info.location == null || info.mode == null) return;
        Villager npc = (Villager) info.location.getWorld().spawnEntity(info.location, EntityType.VILLAGER);
        npc.setAI(false);
        npc.setInvulnerable(true);
        npc.setSilent(true);
        npc.setCollidable(false);
        npc.setCustomName(ChatColor.translateAlternateColorCodes('&', "&a" + capitalize(info.mode)));
        npc.setCustomNameVisible(true);
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public void addNpc(Location location, String mode) {
        NpcInfo info = new NpcInfo(location, mode);
        npcs.add(info);
        spawnNpc(info);
        saveNpcs();
    }

    public void saveNpcs() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (NpcInfo info : npcs) {
            Location loc = info.location;
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("world", loc.getWorld().getName());
            map.put("x", loc.getX());
            map.put("y", loc.getY());
            map.put("z", loc.getZ());
            map.put("yaw", loc.getYaw());
            map.put("pitch", loc.getPitch());
            map.put("mode", info.mode);
            list.add(map);
        }
        config.set("npcs", list);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the mode associated with the given entity if it is a join NPC.
     *
     * @param entity the entity clicked
     * @return mode string or {@code null}
     */
    public String getMode(Entity entity) {
        if (entity.getType() != EntityType.VILLAGER) {
            return null;
        }
        Location loc = entity.getLocation();
        for (NpcInfo info : npcs) {
            Location iLoc = info.location;
            if (loc.getWorld().equals(iLoc.getWorld())
                    && loc.getBlockX() == iLoc.getBlockX()
                    && loc.getBlockY() == iLoc.getBlockY()
                    && loc.getBlockZ() == iLoc.getBlockZ()) {
                return info.mode;
            }
        }
        return null;
    }

    private static class NpcInfo {
        final Location location;
        final String mode;

        NpcInfo(Location location, String mode) {
            this.location = location;
            this.mode = mode;
        }
    }
}
