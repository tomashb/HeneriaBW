package com.heneria.bedwars.managers;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.stats.PlayerStats;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Handles all database operations for the plugin, including table creation
 * and CRUD operations for {@link PlayerStats}.
 */
public class DatabaseManager {

    private final HeneriaBedwars plugin;
    private final boolean usingMySQL;
    private Connection connection;

    public DatabaseManager(HeneriaBedwars plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        this.usingMySQL = "mysql".equalsIgnoreCase(config.getString("database.type", "sqlite"));
        connect(config);
        createTables();
    }

    private void connect(FileConfiguration config) {
        try {
            if (usingMySQL) {
                String host = config.getString("database.mysql.host", "localhost");
                int port = config.getInt("database.mysql.port", 3306);
                String database = config.getString("database.mysql.database", "bedwars");
                String username = config.getString("database.mysql.username", "root");
                String password = config.getString("database.mysql.password", "");
                boolean useSSL = config.getBoolean("database.mysql.useSSL", false);
                String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL + "&serverTimezone=UTC";
                connection = DriverManager.getConnection(url, username, password);
            } else {
                File dataFolder = plugin.getDataFolder();
                if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                    plugin.getLogger().warning("Unable to create plugin data folder for SQLite database");
                }
                File dbFile = new File(dataFolder, "stats.db");
                connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Unable to connect to the database", e);
        }
    }

    private void createTables() {
        if (!isConnectionValid()) {
            plugin.getLogger().severe("Database connection is not available. Tables will not be created.");
            return;
        }

        try {
            plugin.getLogger().info("Starting database initialization...");
            createCoreTablesWithoutFK();
            plugin.getLogger().info("\u2713 Phase 1: Core tables created");

            if (usingMySQL) {
                addAllForeignKeys();
            } else {
                plugin.getLogger().info("Skipping foreign key creation: not supported in SQLite mode");
            }

            verifyTableIntegrity();
            plugin.getLogger().info("\u2713 Phase 3: Database integrity verified");
            plugin.getLogger().info("Database initialization completed successfully!");
        } catch (SQLException e) {
            plugin.getLogger().severe("Critical error during database initialization:");
            plugin.getLogger().severe("Error: " + e.getMessage());
            plugin.getLogger().severe("SQL State: " + e.getSQLState());
            plugin.getLogger().severe("Error Code: " + e.getErrorCode());
            plugin.getLogger().log(Level.SEVERE, "Stack trace:", e);

            plugin.getLogger().warning("Attempting to continue without foreign key constraints...");
            try {
                createCoreTablesWithoutFK();
                plugin.getLogger().info("Basic tables created successfully (without foreign keys)");
            } catch (SQLException fallbackError) {
                plugin.getLogger().severe("Critical failure: Cannot create basic tables");
                throw new RuntimeException("Database initialization completely failed", fallbackError);
            }
        }
    }

    private void createCoreTablesWithoutFK() throws SQLException {
        createPlayersTable();
        createFriendSettingsTable();
        createFriendsTable();
        createGroupsTable();
        createGroupMembersTable();
        createGroupInvitationsTable();
        createClansTable();
        createClanMembersTable();
        createClanRanksTable();
        createClanInvitationsTable();
        createPlayerStatsTable();
    }

    private void createPlayersTable() throws SQLException {
        String sql;
        if (usingMySQL) {
            sql = """
                CREATE TABLE IF NOT EXISTS players (
                    uuid VARCHAR(36) PRIMARY KEY,
                    username VARCHAR(16) NOT NULL,
                    coins BIGINT DEFAULT 1000 NOT NULL,
                    tokens BIGINT DEFAULT 0 NOT NULL,
                    first_join TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    last_join TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    total_playtime BIGINT DEFAULT 0 NOT NULL,
                    discord_id VARCHAR(20) NULL,
                    language VARCHAR(5) NOT NULL DEFAULT 'fr_FR',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_username (username),
                    INDEX idx_last_join (last_join),
                    INDEX idx_discord_id (discord_id),
                    UNIQUE KEY unique_username (username)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
        } else {
            sql = """
                CREATE TABLE IF NOT EXISTS players (
                    uuid TEXT PRIMARY KEY,
                    username TEXT NOT NULL,
                    coins INTEGER DEFAULT 1000 NOT NULL,
                    tokens INTEGER DEFAULT 0 NOT NULL,
                    first_join TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    last_join TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    total_playtime INTEGER DEFAULT 0 NOT NULL,
                    discord_id TEXT NULL,
                    language TEXT NOT NULL DEFAULT 'fr_FR',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
        }

        executeSQL(sql);
        plugin.getLogger().info("\u2713 Players table created");
    }

    private void createFriendSettingsTable() throws SQLException {
        String sql;
        if (usingMySQL) {
            sql = """
                CREATE TABLE IF NOT EXISTS friend_settings (
                    player_uuid VARCHAR(36) PRIMARY KEY,
                    accept_requests ENUM('ALL', 'FRIENDS_OF_FRIENDS', 'NONE') NOT NULL DEFAULT 'ALL',
                    show_online_status BOOLEAN NOT NULL DEFAULT TRUE,
                    receive_notifications BOOLEAN NOT NULL DEFAULT TRUE,
                    auto_accept_friends BOOLEAN NOT NULL DEFAULT FALSE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_accept_requests (accept_requests)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
        } else {
            sql = """
                CREATE TABLE IF NOT EXISTS friend_settings (
                    player_uuid TEXT PRIMARY KEY,
                    accept_requests TEXT NOT NULL DEFAULT 'ALL',
                    show_online_status INTEGER NOT NULL DEFAULT 1,
                    receive_notifications INTEGER NOT NULL DEFAULT 1,
                    auto_accept_friends INTEGER NOT NULL DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
        }

        executeSQL(sql);
        plugin.getLogger().info("\u2713 Friend settings table created");
    }

    private void createFriendsTable() throws SQLException {
        String sql;
        if (usingMySQL) {
            sql = """
                CREATE TABLE IF NOT EXISTS friends (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_uuid VARCHAR(36) NOT NULL,
                    friend_uuid VARCHAR(36) NOT NULL,
                    status ENUM('PENDING', 'ACCEPTED', 'BLOCKED') NOT NULL DEFAULT 'PENDING',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    accepted_at TIMESTAMP NULL,
                    blocked_at TIMESTAMP NULL,
                    UNIQUE KEY unique_friendship (player_uuid, friend_uuid),
                    INDEX idx_player_uuid (player_uuid),
                    INDEX idx_friend_uuid (friend_uuid),
                    INDEX idx_status (status),
                    INDEX idx_created_at (created_at)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
        } else {
            sql = """
                CREATE TABLE IF NOT EXISTS friends (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_uuid TEXT NOT NULL,
                    friend_uuid TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    accepted_at TIMESTAMP NULL,
                    blocked_at TIMESTAMP NULL,
                    UNIQUE (player_uuid, friend_uuid)
                )
                """;
        }

        executeSQL(sql);
        plugin.getLogger().info("\u2713 Friends table created");
    }

    private void createGroupsTable() throws SQLException {
        String sql;
        if (usingMySQL) {
            sql = """
                CREATE TABLE IF NOT EXISTS groups_table (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    leader_uuid VARCHAR(36) NOT NULL,
                    name VARCHAR(50) NULL,
                    description TEXT NULL,
                    max_members INT NOT NULL DEFAULT 8,
                    is_public BOOLEAN NOT NULL DEFAULT FALSE,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    disbanded_at TIMESTAMP NULL,
                    INDEX idx_leader_uuid (leader_uuid),
                    INDEX idx_created_at (created_at),
                    INDEX idx_is_public (is_public)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
        } else {
            sql = """
                CREATE TABLE IF NOT EXISTS groups_table (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    leader_uuid TEXT NOT NULL,
                    name TEXT NULL,
                    description TEXT NULL,
                    max_members INTEGER NOT NULL DEFAULT 8,
                    is_public INTEGER NOT NULL DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    disbanded_at TIMESTAMP NULL
                )
                """;
        }

        executeSQL(sql);
        plugin.getLogger().info("\u2713 Groups table created");
    }

    private void createGroupMembersTable() throws SQLException {
        String sql;
        if (usingMySQL) {
            sql = """
                CREATE TABLE IF NOT EXISTS group_members (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    group_id INT NOT NULL,
                    player_uuid VARCHAR(36) NOT NULL,
                    role ENUM('LEADER', 'MODERATOR', 'MEMBER') NOT NULL DEFAULT 'MEMBER',
                    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE KEY unique_group_member (group_id, player_uuid),
                    INDEX idx_group_id (group_id),
                    INDEX idx_player_uuid (player_uuid),
                    INDEX idx_role (role)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
        } else {
            sql = """
                CREATE TABLE IF NOT EXISTS group_members (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    group_id INTEGER NOT NULL,
                    player_uuid TEXT NOT NULL,
                    role TEXT NOT NULL DEFAULT 'MEMBER',
                    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE (group_id, player_uuid)
                )
                """;
        }

        executeSQL(sql);
        plugin.getLogger().info("\u2713 Group members table created");
    }

    private void createGroupInvitationsTable() throws SQLException {
        String sql;
        if (usingMySQL) {
            sql = """
                CREATE TABLE IF NOT EXISTS group_invitations (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    group_id INT NOT NULL,
                    inviter_uuid VARCHAR(36) NOT NULL,
                    invited_uuid VARCHAR(36) NOT NULL,
                    message TEXT NULL,
                    status ENUM('PENDING', 'ACCEPTED', 'DECLINED', 'EXPIRED') NOT NULL DEFAULT 'PENDING',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    expires_at TIMESTAMP NOT NULL,
                    INDEX idx_group_id (group_id),
                    INDEX idx_inviter_uuid (inviter_uuid),
                    INDEX idx_invited_uuid (invited_uuid),
                    INDEX idx_status (status),
                    INDEX idx_expires_at (expires_at)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
        } else {
            sql = """
                CREATE TABLE IF NOT EXISTS group_invitations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    group_id INTEGER NOT NULL,
                    inviter_uuid TEXT NOT NULL,
                    invited_uuid TEXT NOT NULL,
                    message TEXT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    expires_at TIMESTAMP NOT NULL
                )
                """;
        }

        executeSQL(sql);
        plugin.getLogger().info("\u2713 Group invitations table created");
    }

    private void createClansTable() throws SQLException {
        String sql;
        if (usingMySQL) {
            sql = """
                CREATE TABLE IF NOT EXISTS clans (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(50) UNIQUE NOT NULL,
                    tag VARCHAR(6) UNIQUE NOT NULL,
                    description TEXT NULL,
                    leader_uuid VARCHAR(36) NOT NULL,
                    max_members INT NOT NULL DEFAULT 50,
                    points INT NOT NULL DEFAULT 0,
                    level INT NOT NULL DEFAULT 1,
                    bank_coins BIGINT NOT NULL DEFAULT 0,
                    bank_tokens BIGINT NOT NULL DEFAULT 0,
                    is_public BOOLEAN NOT NULL DEFAULT TRUE,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    disbanded_at TIMESTAMP NULL,
                    INDEX idx_leader_uuid (leader_uuid),
                    INDEX idx_points (points),
                    INDEX idx_level (level),
                    INDEX idx_is_public (is_public),
                    UNIQUE KEY unique_clan_name (name),
                    UNIQUE KEY unique_clan_tag (tag)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
        } else {
            sql = """
                CREATE TABLE IF NOT EXISTS clans (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    tag TEXT NOT NULL UNIQUE,
                    description TEXT NULL,
                    leader_uuid TEXT NOT NULL,
                    max_members INTEGER NOT NULL DEFAULT 50,
                    points INTEGER NOT NULL DEFAULT 0,
                    level INTEGER NOT NULL DEFAULT 1,
                    bank_coins INTEGER NOT NULL DEFAULT 0,
                    bank_tokens INTEGER NOT NULL DEFAULT 0,
                    is_public INTEGER NOT NULL DEFAULT 1,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    disbanded_at TIMESTAMP NULL
                )
                """;
        }

        executeSQL(sql);
        plugin.getLogger().info("\u2713 Clans table created");
    }

    private void createClanMembersTable() throws SQLException {
        String sql;
        if (usingMySQL) {
            sql = """
                CREATE TABLE IF NOT EXISTS clan_members (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    clan_id INT NOT NULL,
                    player_uuid VARCHAR(36) NOT NULL,
                    rank_name VARCHAR(30) NOT NULL DEFAULT 'Membre',
                    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    last_contribution TIMESTAMP NULL,
                    total_contributions BIGINT NOT NULL DEFAULT 0,
                    UNIQUE KEY unique_clan_member (clan_id, player_uuid),
                    INDEX idx_clan_id (clan_id),
                    INDEX idx_player_uuid (player_uuid),
                    INDEX idx_rank_name (rank_name),
                    INDEX idx_total_contributions (total_contributions)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
        } else {
            sql = """
                CREATE TABLE IF NOT EXISTS clan_members (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    clan_id INTEGER NOT NULL,
                    player_uuid TEXT NOT NULL,
                    rank_name TEXT NOT NULL DEFAULT 'Membre',
                    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    last_contribution TIMESTAMP NULL,
                    total_contributions INTEGER NOT NULL DEFAULT 0,
                    UNIQUE (clan_id, player_uuid)
                )
                """;
        }

        executeSQL(sql);
        plugin.getLogger().info("\u2713 Clan members table created");
    }

    private void createClanRanksTable() throws SQLException {
        String sql;
        if (usingMySQL) {
            sql = """
                CREATE TABLE IF NOT EXISTS clan_ranks (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    clan_id INT NOT NULL,
                    name VARCHAR(30) NOT NULL,
                    display_name VARCHAR(50) NOT NULL,
                    priority INT NOT NULL DEFAULT 0,
                    permissions JSON NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE KEY unique_clan_rank (clan_id, name),
                    INDEX idx_clan_id (clan_id),
                    INDEX idx_priority (priority)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
        } else {
            sql = """
                CREATE TABLE IF NOT EXISTS clan_ranks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    clan_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    display_name TEXT NOT NULL,
                    priority INTEGER NOT NULL DEFAULT 0,
                    permissions TEXT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    UNIQUE (clan_id, name)
                )
                """;
        }

        executeSQL(sql);
        plugin.getLogger().info("\u2713 Clan ranks table created");
    }

    private void createClanInvitationsTable() throws SQLException {
        String sql;
        if (usingMySQL) {
            sql = """
                CREATE TABLE IF NOT EXISTS clan_invitations (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    clan_id INT NOT NULL,
                    inviter_uuid VARCHAR(36) NOT NULL,
                    invited_uuid VARCHAR(36) NOT NULL,
                    message TEXT NULL,
                    status ENUM('PENDING', 'ACCEPTED', 'DECLINED', 'EXPIRED') NOT NULL DEFAULT 'PENDING',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    expires_at TIMESTAMP NOT NULL,
                    INDEX idx_clan_id (clan_id),
                    INDEX idx_inviter_uuid (inviter_uuid),
                    INDEX idx_invited_uuid (invited_uuid),
                    INDEX idx_status (status),
                    INDEX idx_expires_at (expires_at)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
        } else {
            sql = """
                CREATE TABLE IF NOT EXISTS clan_invitations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    clan_id INTEGER NOT NULL,
                    inviter_uuid TEXT NOT NULL,
                    invited_uuid TEXT NOT NULL,
                    message TEXT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    expires_at TIMESTAMP NOT NULL
                )
                """;
        }

        executeSQL(sql);
        plugin.getLogger().info("\u2713 Clan invitations table created");
    }

    private void createPlayerStatsTable() throws SQLException {
        String sql;
        if (usingMySQL) {
            sql = """
                CREATE TABLE IF NOT EXISTS player_stats (
                    uuid VARCHAR(36) PRIMARY KEY,
                    username VARCHAR(16),
                    kills INT,
                    deaths INT,
                    wins INT,
                    losses INT,
                    beds_broken INT,
                    games_played INT
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """;
        } else {
            sql = """
                CREATE TABLE IF NOT EXISTS player_stats (
                    uuid TEXT PRIMARY KEY,
                    username TEXT,
                    kills INTEGER,
                    deaths INTEGER,
                    wins INTEGER,
                    losses INTEGER,
                    beds_broken INTEGER,
                    games_played INTEGER
                )
                """;
        }

        executeSQL(sql);
        plugin.getLogger().info("\u2713 Player stats table created");
    }

    private void addAllForeignKeys() {
        plugin.getLogger().info("Adding foreign key constraints...");

        if (!usingMySQL) {
            plugin.getLogger().info("Foreign key creation skipped: only supported for MySQL");
            return;
        }

        try {
            addForeignKeyIfNotExists("friend_settings", "fk_friend_settings_player",
                    "player_uuid", "players", "uuid", "CASCADE");

            addForeignKeyIfNotExists("friends", "fk_friends_player",
                    "player_uuid", "players", "uuid", "CASCADE");
            addForeignKeyIfNotExists("friends", "fk_friends_friend",
                    "friend_uuid", "players", "uuid", "CASCADE");

            addForeignKeyIfNotExists("groups_table", "fk_groups_leader",
                    "leader_uuid", "players", "uuid", "CASCADE");

            addForeignKeyIfNotExists("group_members", "fk_group_members_group",
                    "group_id", "groups_table", "id", "CASCADE");
            addForeignKeyIfNotExists("group_members", "fk_group_members_player",
                    "player_uuid", "players", "uuid", "CASCADE");

            addForeignKeyIfNotExists("group_invitations", "fk_group_invitations_group",
                    "group_id", "groups_table", "id", "CASCADE");
            addForeignKeyIfNotExists("group_invitations", "fk_group_invitations_inviter",
                    "inviter_uuid", "players", "uuid", "CASCADE");
            addForeignKeyIfNotExists("group_invitations", "fk_group_invitations_invited",
                    "invited_uuid", "players", "uuid", "CASCADE");

            addForeignKeyIfNotExists("clans", "fk_clans_leader",
                    "leader_uuid", "players", "uuid", "CASCADE");

            addForeignKeyIfNotExists("clan_members", "fk_clan_members_clan",
                    "clan_id", "clans", "id", "CASCADE");
            addForeignKeyIfNotExists("clan_members", "fk_clan_members_player",
                    "player_uuid", "players", "uuid", "CASCADE");

            addForeignKeyIfNotExists("clan_ranks", "fk_clan_ranks_clan",
                    "clan_id", "clans", "id", "CASCADE");

            addForeignKeyIfNotExists("clan_invitations", "fk_clan_invitations_clan",
                    "clan_id", "clans", "id", "CASCADE");
            addForeignKeyIfNotExists("clan_invitations", "fk_clan_invitations_inviter",
                    "inviter_uuid", "players", "uuid", "CASCADE");
            addForeignKeyIfNotExists("clan_invitations", "fk_clan_invitations_invited",
                    "invited_uuid", "players", "uuid", "CASCADE");

            plugin.getLogger().info("\u2713 All foreign key constraints processed successfully");
        } catch (SQLException e) {
            plugin.getLogger().warning("Some foreign key constraints could not be added: " + e.getMessage());
            plugin.getLogger().info("Database will function normally without foreign key constraints");
        }
    }

    private void addForeignKeyIfNotExists(String table, String constraintName,
                                          String column, String refTable, String refColumn,
                                          String onDelete) throws SQLException {
        String checkSql = """
                SELECT CONSTRAINT_NAME
                FROM information_schema.TABLE_CONSTRAINTS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND CONSTRAINT_NAME = ?
                  AND CONSTRAINT_TYPE = 'FOREIGN KEY'
                """;

        try (PreparedStatement stmt = connection.prepareStatement(checkSql)) {
            stmt.setString(1, table);
            stmt.setString(2, constraintName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    plugin.getLogger().log(Level.FINE, "Foreign key {0} already exists", constraintName);
                    return;
                }
            }
        }

        if (!tableExists(table) || !tableExists(refTable)) {
            plugin.getLogger().warning("Cannot create FK " + constraintName + ": missing table(s)");
            return;
        }

        String alterSql = String.format(
                "ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s (%s) ON DELETE %s ON UPDATE CASCADE",
                table, constraintName, column, refTable, refColumn, onDelete
        );

        executeSQL(alterSql);
        plugin.getLogger().info("\u2713 Added foreign key: " + constraintName);
    }

    private void verifyTableIntegrity() throws SQLException {
        plugin.getLogger().info("Verifying database integrity...");

        String[] requiredTables = {
                "players", "friend_settings", "friends",
                "groups_table", "group_members", "group_invitations",
                "clans", "clan_members", "clan_ranks", "clan_invitations",
                "player_stats"
        };

        for (String tableName : requiredTables) {
            if (!tableExists(tableName)) {
                throw new SQLException("Critical table missing: " + tableName);
            }
        }

        verifyTableStructure("players", "uuid", "username");
        verifyTableStructure("friends", "player_uuid", "friend_uuid");
        verifyTableStructure("groups_table", "leader_uuid");
        verifyTableStructure("clans", "name", "tag");
        verifyTableStructure("player_stats", "uuid");

        plugin.getLogger().info("\u2713 Database integrity verified");
    }

    private void verifyTableStructure(String tableName, String... requiredColumns) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        Set<String> existingColumns = new HashSet<>();

        try (ResultSet columns = metadata.getColumns(null, null, tableName, "%")) {
            while (columns.next()) {
                existingColumns.add(columns.getString("COLUMN_NAME").toLowerCase());
            }
        }

        for (String requiredColumn : requiredColumns) {
            if (!existingColumns.contains(requiredColumn.toLowerCase())) {
                throw new SQLException("Missing column " + requiredColumn + " in table " + tableName);
            }
        }
    }

    private boolean tableExists(String tableName) {
        if (!isConnectionValid()) {
            return false;
        }

        try {
            DatabaseMetaData metadata = connection.getMetaData();
            try (ResultSet tables = metadata.getTables(null, null, tableName, null)) {
                if (tables.next()) {
                    return true;
                }
            }
            try (ResultSet tables = metadata.getTables(null, null, tableName.toUpperCase(), null)) {
                if (tables.next()) {
                    return true;
                }
            }
            try (ResultSet tables = metadata.getTables(null, null, tableName.toLowerCase(), null)) {
                return tables.next();
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to verify existence of table " + tableName + ": " + e.getMessage());
            return false;
        }
    }

    private void executeSQL(String sql) throws SQLException {
        if (!isConnectionValid()) {
            throw new SQLException("No valid database connection available");
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Database connection validation failed", e);
            return false;
        }
    }

    public void dropAllTables() {
        if (!isConnectionValid()) {
            plugin.getLogger().warning("Cannot drop tables: database connection is not available");
            return;
        }

        plugin.getLogger().warning("Dropping all tables for clean reinstall...");

        String disableForeignKeys = usingMySQL ? "SET FOREIGN_KEY_CHECKS = 0" : "PRAGMA foreign_keys = OFF";
        String enableForeignKeys = usingMySQL ? "SET FOREIGN_KEY_CHECKS = 1" : "PRAGMA foreign_keys = ON";

        try {
            executeSQL(disableForeignKeys);
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to disable foreign key checks: " + e.getMessage());
        }

        String[] tables = {
                "clan_invitations", "clan_ranks", "clan_members", "clans",
                "group_invitations", "group_members", "groups_table",
                "friends", "friend_settings", "players", "player_stats"
        };

        for (String table : tables) {
            try {
                executeSQL("DROP TABLE IF EXISTS " + table);
                plugin.getLogger().info("Dropped table: " + table);
            } catch (SQLException e) {
                plugin.getLogger().warning("Could not drop table " + table + ": " + e.getMessage());
            }
        }

        try {
            executeSQL(enableForeignKeys);
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to re-enable foreign key checks: " + e.getMessage());
        }
    }

    public PlayerStats loadStats(UUID uuid, String username) {
        if (!isConnectionValid()) {
            plugin.getLogger().severe("Cannot load stats: database connection is not available");
            return new PlayerStats(uuid, username);
        }

        String select = "SELECT * FROM player_stats WHERE uuid=?";
        try (PreparedStatement ps = connection.prepareStatement(select)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load stats for " + uuid, e);
        }

        PlayerStats stats = new PlayerStats(uuid, username);
        saveStats(stats);
        return stats;
    }

    public void saveStats(PlayerStats stats) {
        if (!isConnectionValid()) {
            plugin.getLogger().severe("Cannot save stats: database connection is not available");
            return;
        }

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
