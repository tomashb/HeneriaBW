package com.heneria.bedwars;

import com.heneria.bedwars.commands.CommandManager;
import com.heneria.bedwars.listeners.ChatListener;
import com.heneria.bedwars.listeners.GUIListener;
import com.heneria.bedwars.listeners.GameListener;
import com.heneria.bedwars.listeners.BlockPlaceListener;
import com.heneria.bedwars.listeners.SetupListener;
import com.heneria.bedwars.listeners.ShopListener;
import com.heneria.bedwars.listeners.UpgradeListener;
import com.heneria.bedwars.listeners.StarterItemListener;
import com.heneria.bedwars.listeners.SpecialItemListener;
import com.heneria.bedwars.listeners.TrapListener;
import com.heneria.bedwars.listeners.StatsListener;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.managers.SetupManager;
import com.heneria.bedwars.managers.GeneratorManager;
import com.heneria.bedwars.managers.ShopManager;
import com.heneria.bedwars.managers.UpgradeManager;
import com.heneria.bedwars.managers.ScoreboardManager;
import com.heneria.bedwars.managers.DatabaseManager;
import com.heneria.bedwars.managers.StatsManager;
import com.heneria.bedwars.managers.EventManager;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class HeneriaBedwars extends JavaPlugin {

    private static HeneriaBedwars instance;
    private ArenaManager arenaManager;
    private SetupManager setupManager;
    private GeneratorManager generatorManager;
    private ShopManager shopManager;
    private UpgradeManager upgradeManager;
    private ScoreboardManager scoreboardManager;
    private EventManager eventManager;
    private DatabaseManager databaseManager;
    private StatsManager statsManager;
    private static NamespacedKey itemTypeKey;

    @Override
    public void onEnable() {
        instance = this;
        itemTypeKey = new NamespacedKey(this, "heneria_item_type");
        saveDefaultConfig();
        MessageManager.init(this);
        getLogger().info("HeneriaBedwars v" + getDescription().getVersion() + " est en cours de chargement...");

        // Initialisation des managers
        this.arenaManager = new ArenaManager(this);
        this.setupManager = new SetupManager();
        this.arenaManager.loadArenas();
        this.generatorManager = new GeneratorManager(this);
        this.shopManager = new ShopManager(this);
        this.upgradeManager = new UpgradeManager(this);
        this.eventManager = new EventManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.statsManager = new StatsManager(this, this.databaseManager);

        // Enregistrement des commandes
        CommandManager commandManager = new CommandManager(this);
        getCommand("bedwars").setExecutor(commandManager);
        getCommand("bedwars").setTabCompleter(commandManager);

        registerListeners();

        getLogger().info("HeneriaBedwars a été activé avec succès.");
    }

    @Override
    public void onDisable() {
        if (statsManager != null) {
            statsManager.saveAll();
        }
        getLogger().info("HeneriaBedwars a été désactivé.");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new SetupListener(this.setupManager), this);
        getServer().getPluginManager().registerEvents(new ShopListener(), this);
        getServer().getPluginManager().registerEvents(new UpgradeListener(), this);
        getServer().getPluginManager().registerEvents(new StarterItemListener(), this);
        getServer().getPluginManager().registerEvents(new SpecialItemListener(), this);
        getServer().getPluginManager().registerEvents(new TrapListener(), this);
        getServer().getPluginManager().registerEvents(new StatsListener(), this);
    }

    public static HeneriaBedwars getInstance() {
        return instance;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public SetupManager getSetupManager() {
        return setupManager;
    }

    public GeneratorManager getGeneratorManager() {
        return generatorManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public StatsManager getStatsManager() {
        return statsManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static NamespacedKey getItemTypeKey() {
        return itemTypeKey;
    }
}
