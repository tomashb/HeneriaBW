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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.EulerAngle;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
            if (mode != null) {
                mode = mode.toUpperCase();
            }
            Object skinObj = map.get("skin");
            String skin = skinObj != null ? String.valueOf(skinObj) : "Steve";
            Object nameObj = map.get("name");
            String name = nameObj != null ? String.valueOf(nameObj) : "&a" + capitalize(mode);
            String itemStr = (String) map.get("item");
            Material item = itemStr != null ? Material.matchMaterial(itemStr) : null;
            String chest = (String) map.get("chestplate");
            Material chestplate = chest != null ? Material.matchMaterial(chest) : null;
            String legs = (String) map.get("leggings");
            Material leggings = legs != null ? Material.matchMaterial(legs) : null;
            String bootsStr = (String) map.get("boots");
            Material boots = bootsStr != null ? Material.matchMaterial(bootsStr) : null;
            if (chestplate == null && leggings == null && boots == null) {
                List<String> armorList = (List<String>) map.get("armor");
                if (armorList != null) {
                    if (armorList.size() > 0) chestplate = Material.matchMaterial(armorList.get(0));
                    if (armorList.size() > 1) leggings = Material.matchMaterial(armorList.get(1));
                    if (armorList.size() > 2) boots = Material.matchMaterial(armorList.get(2));
                }
            }
            NpcInfo info = new NpcInfo(loc, mode, skin, name, item, chestplate, leggings, boots);
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
        npc.getPersistentDataContainer().set(npcKey, PersistentDataType.STRING, "JOIN_NPC:" + info.mode);

        // Head with skin
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        // Only apply a skin owner if the provided name fits within the
        // legacy 16 character limit used by player profiles. This prevents
        // the server from sending an oversized string through the equipment
        // packet which would otherwise result in an EncoderException.
        if (info.skin != null && info.skin.length() <= 16) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(info.skin));
        }
        // Ensure the item itself carries no custom display name; the visible
        // name for the NPC is handled via the ArmorStand's custom name.
        meta.setDisplayName(null);
        head.setItemMeta(meta);
        npc.getEquipment().setHelmet(head);

        if (info.item != null) {
            npc.getEquipment().setItemInMainHand(new ItemStack(info.item));
        }
        // Equip armor pieces if provided
        if (info.chestplate != null) {
            npc.getEquipment().setChestplate(new ItemStack(info.chestplate));
        }
        if (info.leggings != null) {
            npc.getEquipment().setLeggings(new ItemStack(info.leggings));
        }
        if (info.boots != null) {
            npc.getEquipment().setBoots(new ItemStack(info.boots));
        }

        // Apply a more natural default pose
        npc.setHeadPose(new EulerAngle(Math.toRadians(-5), 0, 0));
        npc.setRightArmPose(new EulerAngle(Math.toRadians(-10), 0, 0));
        npc.setLeftArmPose(new EulerAngle(Math.toRadians(-15), 0, 0));
        npc.setRightLegPose(new EulerAngle(Math.toRadians(-2), 0, 0));
        npc.setLeftLegPose(new EulerAngle(Math.toRadians(2), 0, 0));
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public void addNpc(Location location, String mode, String skin, Material item, String name,
                       Material chestplate, Material leggings, Material boots) {
        String upperMode = mode != null ? mode.toUpperCase() : null;
        NpcInfo info = new NpcInfo(location, upperMode, skin, name, item, chestplate, leggings, boots);
        npcs.add(info);
        spawnNpc(info);
        saveNpcs();
    }

    /**
     * Returns an immutable view of the configured lobby NPCs.
     *
     * @return list of NPC information
     */
    public List<NpcInfo> getNpcs() {
        return Collections.unmodifiableList(npcs);
    }

    /**
     * Removes the provided NPC from the world and configuration.
     *
     * @param info the NPC to remove
     */
    public void removeNpc(NpcInfo info) {
        for (Entity entity : info.location.getWorld().getNearbyEntities(info.location, 1, 1, 1)) {
            if (entity.getType() == EntityType.ARMOR_STAND) {
                String tag = entity.getPersistentDataContainer().get(npcKey, PersistentDataType.STRING);
                if (tag != null && tag.equals("JOIN_NPC:" + info.mode)) {
                    entity.remove();
                    break;
                }
            }
        }
        npcs.remove(info);
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
            if (info.chestplate != null) {
                map.put("chestplate", info.chestplate.name());
            }
            if (info.leggings != null) {
                map.put("leggings", info.leggings.name());
            }
            if (info.boots != null) {
                map.put("boots", info.boots.name());
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
     * Removes the nearest join NPC around the given player within the specified radius.
     *
     * @param player the player executing the removal
     * @param radius the search radius in blocks
     * @return {@code true} if an NPC was found and removed
     */
    public boolean removeNearestNpc(Player player, double radius) {
        Location loc = player.getLocation();
        NpcInfo target = null;
        double closest = radius * radius;
        for (NpcInfo info : npcs) {
            if (info.location.getWorld() != loc.getWorld()) continue;
            double dist = info.location.distanceSquared(loc);
            if (dist < closest) {
                closest = dist;
                target = info;
            }
        }
        if (target == null) {
            return false;
        }

        for (Entity entity : target.location.getWorld().getNearbyEntities(target.location, 1, 1, 1)) {
            if (entity.getType() == EntityType.ARMOR_STAND) {
                String tag = entity.getPersistentDataContainer().get(npcKey, PersistentDataType.STRING);
                if (tag != null && tag.equals("JOIN_NPC:" + target.mode)) {
                    entity.remove();
                    break;
                }
            }
        }

        npcs.remove(target);
        saveNpcs();
        return true;
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
        String tag = entity.getPersistentDataContainer().get(npcKey, PersistentDataType.STRING);
        if (tag != null && tag.startsWith("JOIN_NPC:")) {
            return tag.substring("JOIN_NPC:".length());
        }
        return null;
    }

    /**
     * Represents a lobby NPC and its configuration.
     */
    public static class NpcInfo {
        public final Location location;
        public final String mode;
        public final String skin;
        public final String name;
        public final Material item;
        public final Material chestplate;
        public final Material leggings;
        public final Material boots;

        NpcInfo(Location location, String mode, String skin, String name, Material item,
                Material chestplate, Material leggings, Material boots) {
            this.location = location;
            this.mode = mode;
            this.skin = skin;
            this.name = name;
            this.item = item;
            this.chestplate = chestplate;
            this.leggings = leggings;
            this.boots = boots;
        }
    }
}
