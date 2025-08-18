package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.stats.PlayerStats;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Handles the connection to the configured database and basic CRUD
 * operations for {@link PlayerStats}.
 */
public class DatabaseManager {

    private final HeneriaBedwars plugin;
    private Connection connection;

    public DatabaseManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        connect();
        createTable();
    }

    private void connect() {
        FileConfiguration config = plugin.getConfig();
        String type = config.getString("database.type", "sqlite");
        try {
            if ("mysql".equalsIgnoreCase(type)) {
                String host = config.getString("database.mysql.host", "localhost");
                int port = config.getInt("database.mysql.port", 3306);
                String database = config.getString("database.mysql.database", "bedwars");
                String username = config.getString("database.mysql.username", "root");
                String password = config.getString("database.mysql.password", "");
                boolean useSSL = config.getBoolean("database.mysql.useSSL", false);
                String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL;
                connection = DriverManager.getConnection(url, username, password);
            } else {
                File dbFile = new File(plugin.getDataFolder(), "stats.db");
                connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Unable to connect to the database", e);
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_stats (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "username VARCHAR(16)," +
                "kills INT," +
                "deaths INT," +
                "wins INT," +
                "losses INT," +
                "beds_broken INT," +
                "games_played INT" +
                ")";
        try (Statement st = connection.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create player_stats table", e);
        }
    }

    /**
     * Loads the statistics for the given player, creating a new entry if necessary.
     *
     * @param uuid     player UUID
     * @param username current username
     * @return loaded stats
     */
    public PlayerStats loadStats(UUID uuid, String username) {
        String select = "SELECT * FROM player_stats WHERE uuid=?";
        try (PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new PlayerStats(
                        uuid,
                        rs.getString("username"),
                        rs.getInt("kills"),
                        rs.getInt("deaths"),
                        rs.getInt("wins"),
                        rs.getInt("losses"),
                        rs.getInt("beds_broken"),
                        rs.getInt("games_played")
                );
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load stats for " + uuid, e);
        }

        PlayerStats stats = new PlayerStats(uuid, username);
        saveStats(stats);
        return stats;
    }

    /**
     * Saves the given statistics to the database.
     *
     * @param stats stats to save
     */
    public void saveStats(PlayerStats stats) {
        String sql = "REPLACE INTO player_stats (uuid, username, kills, deaths, wins, losses, beds_broken, games_played) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, stats.getUuid().toString());
            ps.setString(2, stats.getUsername());
            ps.setInt(3, stats.getKills());
            ps.setInt(4, stats.getDeaths());
            ps.setInt(5, stats.getWins());
            ps.setInt(6, stats.getLosses());
            ps.setInt(7, stats.getBedsBroken());
            ps.setInt(8, stats.getGamesPlayed());
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save stats for " + stats.getUuid(), e);
        }
    }
}

