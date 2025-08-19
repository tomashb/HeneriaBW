package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Manages persistent join NPCs for the main lobby using Citizens.
 */
public class NpcManager {

    private final HeneriaBedwars plugin;
    private final File file;
    private final YamlConfiguration config;
    private final List<NpcInfo> npcs = new ArrayList<>();
    private final Map<Integer, String> npcModes = new HashMap<>();

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

    private boolean citizensAvailable() {
        return Bukkit.getPluginManager().isPluginEnabled("Citizens");
    }

    public void loadNpcs() {
        npcs.clear();
        npcModes.clear();
        if (!citizensAvailable()) {
            return;
        }
        List<Map<?, ?>> list = config.getMapList("npcs");
        for (Map<?, ?> map : list) {
            String world = (String) map.get("world");
            String mode = (String) map.get("mode");
            String skin = (String) map.get("skin");
            if (world == null || mode == null) continue;
            Location loc = new Location(
                    Bukkit.getWorld(world),
                    getDouble(map, "x"),
                    getDouble(map, "y"),
                    getDouble(map, "z"),
                    (float) getDouble(map, "yaw"),
                    (float) getDouble(map, "pitch"));
            int id = spawnNpc(loc, mode, skin);
            npcs.add(new NpcInfo(loc, mode, skin, id));
        }
        saveNpcs();
    }

    private double getDouble(Map<?, ?> map, String key) {
        Object obj = map.get(key);
        if (obj instanceof Number n) {
            return n.doubleValue();
        }
        return 0;
    }

    private int spawnNpc(Location location, String mode, String skin) {
        if (location == null || mode == null || !citizensAvailable()) {
            return -1;
        }
        NPCRegistry registry = CitizensAPI.getNPCRegistry();
        NPC npc = registry.createNPC(EntityType.PLAYER, ChatColor.translateAlternateColorCodes('&', "&a" + capitalize(mode)));
        npc.setProtected(true);
        SkinTrait trait = npc.getOrAddTrait(SkinTrait.class);
        if (skin != null && !skin.isEmpty()) {
            trait.setSkinName(skin);
        }
        npc.spawn(location);
        npcModes.put(npc.getId(), mode);
        return npc.getId();
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public void addNpc(Location location, String mode, String skin) {
        int id = spawnNpc(location, mode, skin);
        if (id != -1) {
            npcs.add(new NpcInfo(location, mode, skin, id));
            saveNpcs();
        }
    }

    public String getMode(Entity entity) {
        if (!citizensAvailable() || entity == null) {
            return null;
        }
        if (!CitizensAPI.getNPCRegistry().isNPC(entity)) {
            return null;
        }
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
        return npcModes.get(npc.getId());
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
            map.put("skin", info.skin);
            map.put("id", info.id);
            list.add(map);
        }
        config.set("npcs", list);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class NpcInfo {
        final Location location;
        final String mode;
        final String skin;
        final int id;

        NpcInfo(Location location, String mode, String skin, int id) {
            this.location = location;
            this.mode = mode;
            this.skin = skin;
            this.id = id;
        }
    }
}
