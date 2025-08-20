package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

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
    private final NamespacedKey npcKey;

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
        this.npcKey = HeneriaBedwars.getNpcKey();
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
            String skin = (String) map.getOrDefault("skin", "Steve");
            String name = (String) map.getOrDefault("name", "&a" + capitalize(mode));
            String itemStr = (String) map.get("item");
            Material item = itemStr != null ? Material.matchMaterial(itemStr) : null;
            List<String> armorList = (List<String>) map.get("armor");
            List<Material> armor = new ArrayList<>();
            if (armorList != null) {
                for (String s : armorList) {
                    Material m = Material.matchMaterial(s);
                    if (m != null) {
                        armor.add(m);
                    }
                }
            }
            NpcInfo info = new NpcInfo(loc, mode, skin, name, item, armor);
            spawnNpc(info);
            npcs.add(info);
        }
    }

    public void spawnNpc(NpcInfo info) {
        if (info.location == null || info.mode == null) return;
        ArmorStand npc = (ArmorStand) info.location.getWorld().spawnEntity(info.location, EntityType.ARMOR_STAND);
        npc.setInvisible(true);
        npc.setInvulnerable(true);
        npc.setGravity(false);
        npc.setBasePlate(false);
        npc.setArms(true);
        npc.setCustomName(ChatColor.translateAlternateColorCodes('&', info.name));
        npc.setCustomNameVisible(true);
        npc.getPersistentDataContainer().set(npcKey, PersistentDataType.STRING, info.mode);

        // Head with skin
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(info.skin));
        head.setItemMeta(meta);
        npc.getEquipment().setHelmet(head);

        if (info.item != null) {
            npc.getEquipment().setItemInMainHand(new ItemStack(info.item));
        }
        // Equip armor pieces if provided
        if (!info.armor.isEmpty()) {
            if (info.armor.size() > 0 && info.armor.get(0) != null) {
                npc.getEquipment().setChestplate(new ItemStack(info.armor.get(0)));
            }
            if (info.armor.size() > 1 && info.armor.get(1) != null) {
                npc.getEquipment().setLeggings(new ItemStack(info.armor.get(1)));
            }
            if (info.armor.size() > 2 && info.armor.get(2) != null) {
                npc.getEquipment().setBoots(new ItemStack(info.armor.get(2)));
            }
        }
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public void addNpc(Location location, String mode, String skin, Material item, String name, List<Material> armor) {
        NpcInfo info = new NpcInfo(location, mode, skin, name, item, armor);
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
            map.put("skin", info.skin);
            map.put("name", info.name);
            if (info.item != null) {
                map.put("item", info.item.name());
            }
            if (!info.armor.isEmpty()) {
                List<String> armor = new ArrayList<>();
                for (Material m : info.armor) {
                    armor.add(m.name());
                }
                map.put("armor", armor);
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
        if (entity.getType() != EntityType.ARMOR_STAND) {
            return null;
        }
        String mode = entity.getPersistentDataContainer().get(npcKey, PersistentDataType.STRING);
        return mode;
    }

    private static class NpcInfo {
        final Location location;
        final String mode;
        final String skin;
        final String name;
        final Material item;
        final List<Material> armor;

        NpcInfo(Location location, String mode, String skin, String name, Material item, List<Material> armor) {
            this.location = location;
            this.mode = mode;
            this.skin = skin;
            this.name = name;
            this.item = item;
            this.armor = armor;
        }
    }
}
