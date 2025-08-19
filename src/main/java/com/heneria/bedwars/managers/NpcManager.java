package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.utils.MessageManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.traits.SkinTrait;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages persistent NPCs (join and shop) using the Citizens API.
 */
public class NpcManager {

    private final HeneriaBedwars plugin;
    private final File file;
    private final YamlConfiguration config;
    private final boolean citizens;

    private final Map<Integer, String> joinNpcs = new HashMap<>();
    private final Map<Integer, ShopInfo> shopNpcs = new HashMap<>();

    public NpcManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        this.citizens = Bukkit.getPluginManager().getPlugin("Citizens") != null;
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

    public boolean isCitizensEnabled() {
        return citizens;
    }

    private void loadNpcs() {
        joinNpcs.clear();
        shopNpcs.clear();
        List<Map<?, ?>> list = config.getMapList("npcs");
        for (Map<?, ?> map : list) {
            Object idObj = map.get("id");
            if (!(idObj instanceof Number)) continue;
            int id = ((Number) idObj).intValue();
            String type = (String) map.get("type");
            if ("join".equalsIgnoreCase(type)) {
                String mode = (String) map.get("mode");
                joinNpcs.put(id, mode);
            } else if ("shop".equalsIgnoreCase(type)) {
                String team = (String) map.get("team");
                String shopType = (String) map.get("shopType");
                shopNpcs.put(id, new ShopInfo(team, shopType));
            }
        }

        if (citizens) {
            for (int id : joinNpcs.keySet()) {
                NPC npc = CitizensAPI.getNPCRegistry().getById(id);
                if (npc != null) {
                    npc.setProtected(true);
                    if (!npc.isSpawned()) {
                        npc.spawn(npc.getStoredLocation());
                    }
                }
            }
            for (int id : shopNpcs.keySet()) {
                NPC npc = CitizensAPI.getNPCRegistry().getById(id);
                if (npc != null) {
                    npc.setProtected(true);
                    if (!npc.isSpawned()) {
                        npc.spawn(npc.getStoredLocation());
                    }
                }
            }
        }
    }

    private void saveNpcs() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : joinNpcs.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", entry.getKey());
            map.put("type", "join");
            map.put("mode", entry.getValue());
            list.add(map);
        }
        for (Map.Entry<Integer, ShopInfo> entry : shopNpcs.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", entry.getKey());
            map.put("type", "shop");
            map.put("team", entry.getValue().team());
            map.put("shopType", entry.getValue().type());
            list.add(map);
        }
        config.set("npcs", list);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addJoinNpc(Location location, String mode, String skin) {
        if (!citizens) return;
        String name = ChatColor.translateAlternateColorCodes('&', "&a" + capitalize(mode));
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        npc.spawn(location);
        npc.setProtected(true);
        npc.getOrAddTrait(SkinTrait.class).setSkinName(skin);
        joinNpcs.put(npc.getId(), mode.toLowerCase());
        saveNpcs();
    }

    public void addShopNpc(Location location, String team, String type, String skin) {
        if (!citizens) return;
        String baseName = type.equalsIgnoreCase("upgrade") ? MessageManager.get("game.upgrade-npc-name") : MessageManager.get("game.shop-npc-name");
        String name = ChatColor.translateAlternateColorCodes('&', baseName);
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
        npc.spawn(location);
        npc.setProtected(true);
        npc.getOrAddTrait(SkinTrait.class).setSkinName(skin);
        shopNpcs.put(npc.getId(), new ShopInfo(team, type.toLowerCase()));
        saveNpcs();
    }

    public String getJoinMode(Entity entity) {
        if (citizens && entity.hasMetadata("NPC")) {
            NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
            if (npc != null) {
                return joinNpcs.get(npc.getId());
            }
        }
        for (String tag : entity.getScoreboardTags()) {
            if (tag.startsWith("joinnpc_")) {
                return tag.substring("joinnpc_".length());
            }
        }
        return null;
    }

    public ShopInfo getShopInfo(Entity entity) {
        if (citizens && entity.hasMetadata("NPC")) {
            NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
            if (npc != null) {
                return shopNpcs.get(npc.getId());
            }
        }
        return null;
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    /**
     * Information about a shop NPC.
     *
     * @param team team identifier
     * @param type shop type (item or upgrade)
     */
    public record ShopInfo(String team, String type) {
    }
}

