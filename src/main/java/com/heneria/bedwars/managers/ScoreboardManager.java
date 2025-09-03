package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.managers.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import com.heneria.bedwars.utils.MessageManager;
import com.heneria.bedwars.stats.PlayerStats;
import me.clip.placeholderapi.PlaceholderAPI;

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
    private String mainLobbyTitle;
    private List<String> mainLobbyLines;
    private String lobbyTitle;
    private List<String> lobbyLines;
    private String gameTitle;
    private List<String> gameLines;
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
        this.mainLobbyTitle = config.getString("main-lobby.title");
        this.mainLobbyLines = config.getStringList("main-lobby.lines");
        this.lobbyTitle = config.getString("lobby.title");
        this.lobbyLines = config.getStringList("lobby.lines");
        this.gameTitle = config.getString("game.title");
        this.gameLines = config.getStringList("game.lines");
        this.teamLineFormat = config.getString("team-line-format", "{team_color_code}{team_icon} {team_bed_status} &f{team_players_alive} {you_marker}");
    }

    private void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateAll();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void setScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        String initialTitle = mainLobbyTitle != null ? mainLobbyTitle : (lobbyTitle != null ? lobbyTitle : (gameTitle != null ? gameTitle : "BedWars"));
        Objective obj = board.registerNewObjective("hbw", "dummy", ChatColor.translateAlternateColorCodes('&', initialTitle));
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        setBoard(player, board);
    }

    public void setBoard(Player player, Scoreboard board) {
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
            updatePlayer(p);
        }
    }

    public void updatePlayer(Player player) {
        Scoreboard board = boards.get(player.getUniqueId());
        if (board == null) return;
        Arena arena = plugin.getArenaManager().getArena(player);
        Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
        if (obj == null) {
            String defaultTitle = mainLobbyTitle != null ? mainLobbyTitle : (lobbyTitle != null ? lobbyTitle : (gameTitle != null ? gameTitle : "BedWars"));
            obj = board.registerNewObjective("hbw", "dummy", ChatColor.translateAlternateColorCodes('&', defaultTitle));
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        GameState state = arena != null ? arena.getState() : null;
        String title;
        List<String> lines;
        if (arena == null) {
            title = mainLobbyTitle;
            lines = mainLobbyLines;
        } else if (state == GameState.WAITING || state == GameState.STARTING) {
            title = lobbyTitle != null ? lobbyTitle : gameTitle;
            lines = (lobbyLines != null && !lobbyLines.isEmpty()) ? lobbyLines : gameLines;
        } else {
            title = gameTitle != null ? gameTitle : lobbyTitle;
            lines = (gameLines != null && !gameLines.isEmpty()) ? gameLines : lobbyLines;
        }
        if (title == null || lines == null) {
            return;
        }
        obj.setDisplayName(ChatColor.translateAlternateColorCodes('&', replacePlaceholders(title, player, arena)));

        for (String entry : new HashSet<>(board.getEntries())) {
            board.resetScores(entry);
        }

        List<String> finalLines = buildLines(lines, player, arena);
        int score = finalLines.size();
        for (String line : finalLines) {
            line = ChatColor.translateAlternateColorCodes('&', line);
            obj.getScore(line).setScore(score--);
        }
    }

    private List<String> buildLines(List<String> baseLines, Player player, Arena arena) {
        List<String> result = new ArrayList<>();
        for (String line : baseLines) {
            if (line.contains("{team_status}")) {
                if (arena != null) {
                    for (String teamLine : buildTeamLines(player, arena)) {
                        result.add(line.replace("{team_status}", teamLine));
                    }
                }
                continue;
            }
            result.add(replacePlaceholders(line, player, arena));
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
            String bedStatus = team.hasBed() ? MessageManager.get("scoreboard.bed-alive") : MessageManager.get("scoreboard.bed-destroyed");
            long alive = arena.getAlivePlayers().stream().filter(team::isMember).count();
            String you = team.equals(playerTeam) ? MessageManager.get("scoreboard.you-marker") : "";
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
        text = text.replace("{date}", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .replace("{player}", player.getName());

        PlayerStats stats = plugin.getStatsManager().getStats(player);
        if (stats != null) {
            text = text.replace("{wins}", String.valueOf(stats.getWins()))
                    .replace("{kills}", String.valueOf(stats.getKills()))
                    .replace("{beds_broken}", String.valueOf(stats.getBedsBroken()))
                    .replace("{deaths}", String.valueOf(stats.getDeaths()))
                    .replace("{losses}", String.valueOf(stats.getLosses()))
                    .replace("{games_played}", String.valueOf(stats.getGamesPlayed()));
        }

        if (arena != null) {
            text = text.replace("{next_event_name}", getNextEventName(arena))
                    .replace("{next_event_time}", getNextEventTime(arena))
                    .replace("{map_name}", arena.getName())
                    .replace("{current_players}", String.valueOf(arena.getPlayers().size()))
                    .replace("{max_players}", String.valueOf(arena.getMaxPlayers()))
                    .replace("{status}", getLobbyStatus(arena));
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }

    private String getLobbyStatus(Arena arena) {
        GameState state = arena.getState();
        if (state == GameState.STARTING) {
            return MessageManager.get("scoreboard.lobby-starting", "time", String.valueOf(arena.getCountdownTime()));
        }
        if (state == GameState.WAITING) {
            return MessageManager.get("scoreboard.lobby-waiting");
        }
        return "";
    }

    private String getNextEventName(Arena arena) {
        EventManager em = plugin.getEventManager();
        if (em == null) {
            return "N/A";
        }
        return em.getNextEventName(arena);
    }

    private String getNextEventTime(Arena arena) {
        EventManager em = plugin.getEventManager();
        if (em == null) {
            return "--";
        }
        return em.getNextEventTime(arena);
    }
}
