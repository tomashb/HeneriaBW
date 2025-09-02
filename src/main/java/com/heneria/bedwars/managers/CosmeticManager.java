package com.heneria.bedwars.managers;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages cosmetic selections for players such as NPC skins,
 * kill effects, bed break effects and custom messages.
 */
public class CosmeticManager {

    public enum NpcSkin {
        VILLAGER("MHF_Villager"),
        ZOMBIE("MHF_Zombie");

        private final String owner;

        NpcSkin(String owner) {
            this.owner = owner;
        }

        public String getOwner() {
            return owner;
        }

        public void apply(ArmorStand npc) {
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
            head.setItemMeta(meta);
            npc.getEquipment().setHelmet(head);
        }
    }

    public enum KillEffect {
        LIGHTNING {
            @Override
            public void play(Location loc) {
                if (loc.getWorld() != null) {
                    loc.getWorld().strikeLightningEffect(loc);
                }
            }
        },
        EXPLOSION {
            @Override
            public void play(Location loc) {
                if (loc.getWorld() != null) {
                    loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 1);
                    loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                }
            }
        };

        public abstract void play(Location loc);
    }

    public enum BedBreakEffect {
        FIREWORK {
            @Override
            public void play(Location loc) {
                if (loc.getWorld() != null) {
                    loc.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc.add(0.5,0.5,0.5), 30, 0.3,0.3,0.3);
                    loc.getWorld().playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1f);
                }
            }
        },
        SMOKE {
            @Override
            public void play(Location loc) {
                if (loc.getWorld() != null) {
                    loc.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc.add(0.5,0.5,0.5), 20, 0.3,0.3,0.3);
                    loc.getWorld().playSound(loc, Sound.BLOCK_CAMPFIRE_SMOKE, 1f, 1f);
                }
            }
        };

        public abstract void play(Location loc);
    }

    private final Map<UUID, NpcSkin> npcSkins = new HashMap<>();
    private final Map<UUID, KillEffect> killEffects = new HashMap<>();
    private final Map<UUID, BedBreakEffect> bedBreakEffects = new HashMap<>();
    private final Map<UUID, String> killMessages = new HashMap<>();
    private final Map<UUID, String> bedBreakMessages = new HashMap<>();

    public void setNpcSkin(UUID uuid, NpcSkin skin) {
        npcSkins.put(uuid, skin);
    }

    public NpcSkin getNpcSkin(UUID uuid) {
        return npcSkins.get(uuid);
    }

    public void setKillEffect(UUID uuid, KillEffect effect) {
        killEffects.put(uuid, effect);
    }

    public KillEffect getKillEffect(UUID uuid) {
        return killEffects.get(uuid);
    }

    public void setBedBreakEffect(UUID uuid, BedBreakEffect effect) {
        bedBreakEffects.put(uuid, effect);
    }

    public BedBreakEffect getBedBreakEffect(UUID uuid) {
        return bedBreakEffects.get(uuid);
    }

    public void setKillMessage(UUID uuid, String message) {
        killMessages.put(uuid, message);
    }

    public String getKillMessage(UUID uuid) {
        return killMessages.get(uuid);
    }

    public void setBedBreakMessage(UUID uuid, String message) {
        bedBreakMessages.put(uuid, message);
    }

    public String getBedBreakMessage(UUID uuid) {
        return bedBreakMessages.get(uuid);
    }
}

