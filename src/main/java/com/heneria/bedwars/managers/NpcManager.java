package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.traits.SkinTrait;
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
    private final boolean citizensAvailable;

    public NpcManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        this.citizensAvailable = Bukkit.getPluginManager().isPluginEnabled("Citizens");
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
            int id = map.containsKey("id") ? ((Number) map.get("id")).intValue() : -1;
            String skin = (String) map.get("skin");
            NpcInfo info = new NpcInfo(loc, mode, id, skin);
            spawnNpc(info);
            npcs.add(info);
        }
    }

    public void spawnNpc(NpcInfo info) {
        if (info.location == null || info.mode == null) return;
        if (citizensAvailable) {
            String name = ChatColor.translateAlternateColorCodes('&', "&a" + capitalize(info.mode));
            NPC npc;
            if (info.id >= 0) {
                npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, info.id, name);
            } else {
                npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
                info.id = npc.getId();
            }
            npc.setProtected(true);
            if (info.skin != null) {
                npc.getOrAddTrait(SkinTrait.class).setSkinName(info.skin);
            }
            npc.spawn(info.location);
        } else {
            Villager npc = (Villager) info.location.getWorld().spawnEntity(info.location, EntityType.VILLAGER);
            npc.setAI(false);
            npc.setInvulnerable(true);
            npc.setSilent(true);
            npc.setCollidable(false);
            npc.addScoreboardTag("joinnpc_" + info.mode.toLowerCase());
            npc.setCustomName(ChatColor.translateAlternateColorCodes('&', "&a" + capitalize(info.mode)));
            npc.setCustomNameVisible(true);
        }
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public void addNpc(Location location, String mode, String skin) {
        NpcInfo info = new NpcInfo(location, mode, -1, skin);
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
            if (info.id >= 0) {
                map.put("id", info.id);
            }
            if (info.skin != null) {
                map.put("skin", info.skin);
            }
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
        if (citizensAvailable && CitizensAPI.getNPCRegistry().isNPC(entity)) {
            NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
            for (NpcInfo info : npcs) {
                if (info.id == npc.getId()) {
                    return info.mode;
                }
            }
            return null;
        }
        for (String tag : entity.getScoreboardTags()) {
            if (tag.startsWith("joinnpc_")) {
                return tag.substring("joinnpc_".length());
            }
        }
        return null;
    }

    private static class NpcInfo {
        final Location location;
        final String mode;
        int id;
        final String skin;

        NpcInfo(Location location, String mode, int id, String skin) {
            this.location = location;
            this.mode = mode;
            this.id = id;
            this.skin = skin;
        }
    }
}
