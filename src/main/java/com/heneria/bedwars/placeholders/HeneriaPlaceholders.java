package com.heneria.bedwars.placeholders;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.stats.PlayerStats;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.util.Locale;

/**
 * PlaceholderAPI expansion to expose player statistics.
 */
public class HeneriaPlaceholders extends PlaceholderExpansion {

    private final HeneriaBedwars plugin;

    public HeneriaPlaceholders(HeneriaBedwars plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "heneriabw";
    }

    @Override
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) {
            return "0";
        }

        PlayerStats stats = plugin.getStatsManager().getStats(player.getUniqueId());
        if (stats == null) {
            return "0";
        }

        switch (params.toLowerCase(Locale.ROOT)) {
            case "kills":
                return String.valueOf(stats.getKills());
            case "deaths":
                return String.valueOf(stats.getDeaths());
            case "wins":
                return String.valueOf(stats.getWins());
            case "losses":
                return String.valueOf(stats.getLosses());
            case "beds_broken":
                return String.valueOf(stats.getBedsBroken());
            case "games_played":
                return String.valueOf(stats.getGamesPlayed());
            case "kdr":
                int deaths = stats.getDeaths();
                if (deaths == 0) {
                    return String.valueOf(stats.getKills());
                }
                double kdr = (double) stats.getKills() / deaths;
                return String.format(Locale.US, "%.2f", kdr);
            default:
                return null;
        }
    }

    @Override
    public boolean persist() {
        return true;
    }
}
