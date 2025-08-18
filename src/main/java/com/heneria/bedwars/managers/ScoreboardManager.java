package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Handles creation and updating of in-game scoreboards.
 */
public class ScoreboardManager {

    private final HeneriaBedwars plugin;
    private final Map<UUID, Scoreboard> boards = new HashMap<>();
    private String title;
    private List<String> lines;
    private String teamLineFormat;

    public ScoreboardManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        loadConfig();
        startTask();
    }

    private void loadConfig() {
        File file = new File(plugin.getDataFolder(), "scoreboard.yml");
        if (!file.exists()) {
            plugin.saveResource("scoreboard.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.title = config.getString("title", "BedWars");
        this.lines = config.getStringList("lines");
        this.teamLineFormat = config.getString("team-line-format", "{team_color_code}{team_icon} {team_bed_status} &f{team_players_alive} {you_marker}");
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateAll();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    public void setScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("hbw", "dummy", ChatColor.translateAlternateColorCodes('&', title));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        boards.put(player.getUniqueId(), board);
        player.setScoreboard(board);
        updatePlayer(player);
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        boards.remove(player.getUniqueId());
    }

    private void updateAll() {
        Iterator<UUID> it = boards.keySet().iterator();
        while (it.hasNext()) {
            UUID id = it.next();
            Player p = Bukkit.getPlayer(id);
            if (p == null || !p.isOnline()) {
                it.remove();
                continue;
            }
            Arena arena = plugin.getArenaManager().getArena(p);
            if (arena == null) {
                removeScoreboard(p);
                it.remove();
                continue;
            }
            updatePlayer(p);
        }
    }

    public void updatePlayer(Player player) {
        Scoreboard board = boards.get(player.getUniqueId());
        if (board == null) return;
        Arena arena = plugin.getArenaManager().getArena(player);
        if (arena == null) return;
        Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
        if (obj == null) {
            obj = board.registerNewObjective("hbw", "dummy", ChatColor.translateAlternateColorCodes('&', title));
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', replacePlaceholders(title, player, arena)));

        for (String entry : new HashSet<>(board.getEntries())) {
            board.resetScores(entry);
        }

        List<String> finalLines = buildLines(player, arena);
        int score = finalLines.size();
        for (String line : finalLines) {
            line = ChatColor.translateAlternateColorCodes('&', line);
            obj.getScore(line).setScore(score--);
        }
    }

    private List<String> buildLines(Player player, Arena arena) {
        List<String> result = new ArrayList<>();
        for (String line : lines) {
            if (line.contains("{team_status}")) {
                for (String teamLine : buildTeamLines(player, arena)) {
                    result.add(line.replace("{team_status}", teamLine));
                }
            } else {
                result.add(replacePlaceholders(line, player, arena));
            }
        }
        return result;
    }

    private List<String> buildTeamLines(Player player, Arena arena) {
        List<String> list = new ArrayList<>();
        Team playerTeam = arena.getTeam(player);
        for (Team team : arena.getTeams().values()) {
            TeamColor color = team.getColor();
            String colorCode = color.getChatColor().toString();
            String icon = color.name().substring(0, 1);
            String bedStatus = team.hasBed() ? "§a✔" : "§c❌";
            long alive = arena.getAlivePlayers().stream().filter(team::isMember).count();
            String you = team.equals(playerTeam) ? "§e(VOUS)" : "";
            String line = teamLineFormat
                    .replace("{team_color_code}", colorCode)
                    .replace("{team_icon}", icon)
                    .replace("{team_bed_status}", bedStatus)
                    .replace("{team_players_alive}", String.valueOf(alive))
                    .replace("{you_marker}", you);
            list.add(line);
        }
        return list;
    }

    private String replacePlaceholders(String text, Player player, Arena arena) {
        return text
                .replace("{date}", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .replace("{next_event_name}", getNextEventName(arena))
                .replace("{next_event_time}", getNextEventTime(arena));
    }

    private String getNextEventName(Arena arena) {
        // Placeholder for future event system
        return "N/A";
    }

    private String getNextEventTime(Arena arena) {
        // Placeholder for future event system
        return "--";
    }
}
