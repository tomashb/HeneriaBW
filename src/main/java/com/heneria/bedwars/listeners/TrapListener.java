package com.heneria.bedwars.listeners;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.managers.UpgradeManager;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

/**
 * Handles detection of enemies entering a team's base and triggering traps.
 */
public class TrapListener implements Listener {

    private final HeneriaBedwars plugin = HeneriaBedwars.getInstance();
    private final UpgradeManager upgradeManager = plugin.getUpgradeManager();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Arena arena = plugin.getArenaManager().getArenaByPlayer(player.getUniqueId());
        if (arena == null) {
            return;
        }
        Team playerTeam = arena.getTeam(player);
        if (playerTeam == null) {
            return;
        }
        int radius = plugin.getConfig().getInt("trap-radius", 10);
        double radiusSquared = radius * radius;

        for (Team team : arena.getTeams().values()) {
            if (team == playerTeam) {
                continue;
            }
            Location bed = team.getBedLocation();
            if (bed == null) {
                continue;
            }
            double fromDist = event.getFrom().distanceSquared(bed);
            double toDist = event.getTo().distanceSquared(bed);
            if (fromDist > radiusSquared && toDist <= radiusSquared) {
                // player entered base
                Map<String, Boolean> traps = team.getTraps();
                boolean triggered = false;
                for (Map.Entry<String, Boolean> entry : traps.entrySet()) {
                    if (!entry.getValue()) {
                        continue;
                    }
                    UpgradeManager.Trap trap = upgradeManager.getTrap(entry.getKey());
                    if (trap == null) {
                        continue;
                    }
                    if (entry.getKey().equals("counter-offensive-trap")) {
                        for (UUID uuid : team.getMembers()) {
                            Player p = Bukkit.getPlayer(uuid);
                            if (p != null) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 15 * 20, 1, true, true, true));
                                p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 15 * 20, 1, true, true, true));
                            }
                        }
                    } else if (entry.getKey().equals("reveal-trap")) {
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, trap.duration() * 20, 0, true, true, true));
                    } else {
                        upgradeManager.applyTrapEffect(player, trap);
                    }
                    team.setTrapActive(entry.getKey(), false);
                    MessageManager.sendMessage(player, "game.trap-triggered-attacker", "trap", trap.name());
                    Sound alarm = team.hasTrapAlarm() ? Sound.valueOf(upgradeManager.getTrapAlarmSound()) : null;
                    for (UUID uuid : team.getMembers()) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p != null) {
                            MessageManager.sendMessage(p, "game.trap-triggered-defender", "player", player.getName(), "trap", trap.name());
                            if (alarm != null) {
                                p.playSound(p.getLocation(), alarm, 1f, 1f);
                            }
                        }
                    }
                    triggered = true;
                    break;
                }
                if (triggered) {
                    break;
                }
            }
        }
    }
}

