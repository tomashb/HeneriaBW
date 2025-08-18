package com.heneria.bedwars.commands.subcommands;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.managers.StatsManager;
import com.heneria.bedwars.stats.PlayerStats;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Command allowing players to view their statistics.
 */
public class StatsCommand implements SubCommand {

    private final StatsManager statsManager = HeneriaBedwars.getInstance().getStatsManager();

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public void execute(Player player, String[] args) {
        Player target = player;
        if (args.length > 0) {
            if (!player.hasPermission("heneriabw.admin.stats")) {
                MessageManager.sendMessage(player, "errors.no-permission");
                return;
            }
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                MessageManager.sendMessage(player, "errors.player-not-found");
                return;
            }
        }

        PlayerStats stats = statsManager.getStats(target.getUniqueId());
        if (stats == null) {
            MessageManager.sendMessage(player, "errors.player-not-found");
            return;
        }

        for (String line : MessageManager.getList("stats.format")) {
            String formatted = line
                    .replace("{player}", target.getName())
                    .replace("{kills}", String.valueOf(stats.getKills()))
                    .replace("{deaths}", String.valueOf(stats.getDeaths()))
                    .replace("{wins}", String.valueOf(stats.getWins()))
                    .replace("{losses}", String.valueOf(stats.getLosses()))
                    .replace("{beds_broken}", String.valueOf(stats.getBedsBroken()))
                    .replace("{games_played}", String.valueOf(stats.getGamesPlayed()));
            player.sendMessage(formatted);
        }
    }

    @Override
    public List<String> tabComplete(Player player, String[] args) {
        if (args.length == 1 && player.hasPermission("heneriabw.admin.stats")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    names.add(p.getName());
                }
            }
            return names;
        }
        return new ArrayList<>();
    }
}

