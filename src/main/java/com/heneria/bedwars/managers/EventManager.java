package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.enums.GeneratorType;
import com.heneria.bedwars.events.GameEventType;
import com.heneria.bedwars.events.TimedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

/**
 * Loads the game event configuration and manages timelines for active arenas.
 */
public class EventManager {

    private final HeneriaBedwars plugin;
    private final List<TimedEvent> templateEvents = new ArrayList<>();
    private final Map<Arena, ArenaTimeline> timelines = new HashMap<>();

    public EventManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        File file = new File(plugin.getDataFolder(), "events.yml");
        if (!file.exists()) {
            plugin.saveResource("events.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        templateEvents.clear();
        if (config.contains("game-events")) {
            for (Map<?, ?> map : config.getMapList("game-events")) {
                String timeStr = String.valueOf(map.get("time"));
                int time = parseTime(timeStr);
                GameEventType type = GameEventType.valueOf(String.valueOf(map.get("type")).toUpperCase());
                List<GeneratorType> targets = new ArrayList<>();
                Object targetsObj = map.get("targets");
                if (targetsObj instanceof List<?> list) {
                    for (Object o : list) {
                        targets.add(GeneratorType.valueOf(String.valueOf(o).toUpperCase()));
                    }
                }
                int tier = map.get("new-tier") instanceof Number n ? n.intValue() : 1;
                String message = map.get("broadcast-message") == null ? "" : String.valueOf(map.get("broadcast-message"));
                int amount = map.get("amount") instanceof Number a ? a.intValue() : (type == GameEventType.SPAWN_DRAGONS ? 1 : 0);
                String location = map.get("location") == null ? null : String.valueOf(map.get("location"));
                templateEvents.add(new TimedEvent(time, type, targets, tier, message, amount, location));
            }
            templateEvents.sort(Comparator.comparingInt(TimedEvent::getTime));
        }
    }

    private int parseTime(String time) {
        String t = time.trim().toLowerCase();
        if (t.endsWith("m")) {
            return Integer.parseInt(t.substring(0, t.length() - 1)) * 60;
        }
        if (t.endsWith("s")) {
            return Integer.parseInt(t.substring(0, t.length() - 1));
        }
        return Integer.parseInt(t);
    }

    public void startTimeline(Arena arena) {
        ArenaTimeline task = new ArenaTimeline(arena, templateEvents);
        task.runTaskTimer(plugin, 20L, 20L);
        timelines.put(arena, task);
    }

    public void stopTimeline(Arena arena) {
        ArenaTimeline task = timelines.remove(arena);
        if (task != null) {
            task.cancel();
        }
    }

    public String getNextEventName(Arena arena) {
        ArenaTimeline task = timelines.get(arena);
        if (task == null) {
            return "N/A";
        }
        TimedEvent ev = task.next;
        if (ev == null) {
            return "N/A";
        }
        if (ev.getType() == GameEventType.UPGRADE_GENERATORS && !ev.getTargets().isEmpty()) {
            GeneratorType type = ev.getTargets().get(0);
            String name = type.name().substring(0, 1) + type.name().substring(1).toLowerCase();
            return name + " " + toRoman(ev.getNewTier());
        }
        return ev.getType().name();
    }

    public String getNextEventTime(Arena arena) {
        ArenaTimeline task = timelines.get(arena);
        if (task == null) {
            return "--";
        }
        int remaining = task.getRemaining();
        if (remaining < 0) {
            return "--";
        }
        int minutes = remaining / 60;
        int seconds = remaining % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private String toRoman(int value) {
        return switch (value) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(value);
        };
    }

    private class ArenaTimeline extends BukkitRunnable {
        private final Arena arena;
        private final Queue<TimedEvent> events;
        private TimedEvent next;
        private int elapsed = 0;

        ArenaTimeline(Arena arena, List<TimedEvent> template) {
            this.arena = arena;
            this.events = new LinkedList<>(template);
            this.next = events.poll();
        }

        @Override
        public void run() {
            elapsed++;
            if (next != null && elapsed >= next.getTime()) {
                trigger(next);
                next = events.poll();
            }
        }

        private void trigger(TimedEvent event) {
            if (event.getType() == GameEventType.UPGRADE_GENERATORS) {
                for (GeneratorType type : event.getTargets()) {
                    for (Generator gen : arena.getGenerators()) {
                        if (gen.getType() == type) {
                            gen.setTier(event.getNewTier());
                            plugin.getGeneratorManager().registerGenerator(gen);
                        }
                    }
                }
            } else if (event.getType() == GameEventType.SUDDEN_DEATH) {
                arena.destroyAllBeds();
            } else if (event.getType() == GameEventType.SPAWN_DRAGONS) {
                plugin.getLogger().info("EventManager: SPAWN_DRAGONS triggered for arena " + arena.getName());
                int amount = Math.max(1, event.getAmount());
                for (int i = 0; i < amount; i++) {
                    arena.spawnDragon();
                }
            } else if (event.getType() == GameEventType.SPAWN_SPECIAL_NPC) {
                arena.spawnSpecialNpc();
            } else if (event.getType() == GameEventType.DESPAWN_SPECIAL_NPC) {
                arena.despawnSpecialNpc();
            }
            String msg = ChatColor.translateAlternateColorCodes('&', event.getMessage());
            for (UUID id : arena.getPlayers()) {
                Player p = Bukkit.getPlayer(id);
                if (p != null) {
                    p.sendMessage(msg);
                    p.sendTitle("", msg, 10, 70, 20);
                }
            }
        }

        private int getRemaining() {
            if (next == null) {
                return -1;
            }
            return next.getTime() - elapsed;
        }
    }
}

