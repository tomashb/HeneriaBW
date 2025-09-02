package com.heneria.bedwars;

import com.heneria.bedwars.commands.CommandManager;
import com.heneria.bedwars.listeners.ChatListener;
import com.heneria.bedwars.listeners.GUIListener;
import com.heneria.bedwars.listeners.GameListener;
import com.heneria.bedwars.listeners.BlockPlaceListener;
import com.heneria.bedwars.listeners.SetupListener;
import com.heneria.bedwars.listeners.NpcListener;
import com.heneria.bedwars.listeners.StarterItemListener;
import com.heneria.bedwars.listeners.SpecialItemListener;
import com.heneria.bedwars.listeners.SpecialNpcListener;
import com.heneria.bedwars.listeners.GolemListener;
import com.heneria.bedwars.listeners.TrapListener;
import com.heneria.bedwars.listeners.StatsListener;
import com.heneria.bedwars.listeners.HungerListener;
import com.heneria.bedwars.listeners.VoidKillListener;
import com.heneria.bedwars.listeners.LobbyProtectionListener;
import com.heneria.bedwars.listeners.TeamSelectorListener;
import com.heneria.bedwars.listeners.HealerMilkListener;
import com.heneria.bedwars.listeners.TemperedGlassListener;
import com.heneria.bedwars.listeners.LeaveItemListener;
import com.heneria.bedwars.listeners.MainLobbyListener;
import com.heneria.bedwars.listeners.ReconnectListener;
import com.heneria.bedwars.listeners.JoinQuitMessageListener;
import com.heneria.bedwars.listeners.LobbyVoidListener;
import com.heneria.bedwars.listeners.PvpListener;
import com.heneria.bedwars.managers.ArenaManager;
import com.heneria.bedwars.managers.SetupManager;
import com.heneria.bedwars.managers.GeneratorManager;
import com.heneria.bedwars.managers.ShopManager;
import com.heneria.bedwars.managers.SpecialShopManager;
import com.heneria.bedwars.managers.UpgradeManager;
import com.heneria.bedwars.managers.ScoreboardManager;
import com.heneria.bedwars.managers.DatabaseManager;
import com.heneria.bedwars.managers.StatsManager;
import com.heneria.bedwars.managers.EventManager;
import com.heneria.bedwars.managers.PlayerProgressionManager;
import com.heneria.bedwars.managers.BountyManager;
import com.heneria.bedwars.managers.NpcManager;
import com.heneria.bedwars.managers.NpcAnimationManager;
import com.heneria.bedwars.managers.ReconnectManager;
import com.heneria.bedwars.managers.HologramManager;
import com.heneria.bedwars.managers.TablistManager;
import com.heneria.bedwars.utils.MessageManager;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;

public final class HeneriaBedwars extends JavaPlugin {

    private static HeneriaBedwars instance;
    private ArenaManager arenaManager;
    private SetupManager setupManager;
    private GeneratorManager generatorManager;
    private HologramManager hologramManager;
    private ShopManager shopManager;
    private SpecialShopManager specialShopManager;
    private UpgradeManager upgradeManager;
    private ScoreboardManager scoreboardManager;
    private TablistManager tablistManager;
    private EventManager eventManager;
    private DatabaseManager databaseManager;
    private StatsManager statsManager;
    private PlayerProgressionManager playerProgressionManager;
    private BountyManager bountyManager;
    private NpcManager npcManager;
    private NpcAnimationManager npcAnimationManager;
    private ReconnectManager reconnectManager;
    private Location mainLobby;
    private static NamespacedKey itemTypeKey;
    private static NamespacedKey npcKey;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        itemTypeKey = new NamespacedKey(this, "heneria_item_type");
        npcKey = new NamespacedKey(this, "heneria_npc_type");
        saveDefaultConfig();
        saveResource("lobby_shop.yml", false);
        mainLobby = getConfig().getLocation("main-lobby");
        MessageManager.init(this);
        getLogger().info("HeneriaBedwars v" + getDescription().getVersion() + " est en cours de chargement...");

        // Initialisation des managers
        this.arenaManager = new ArenaManager(this);
        this.setupManager = new SetupManager();
        this.arenaManager.loadArenas();
        this.generatorManager = new GeneratorManager(this);
        this.hologramManager = new HologramManager();
        this.shopManager = new ShopManager(this);
        this.specialShopManager = new SpecialShopManager(this);
        this.upgradeManager = new UpgradeManager(this);
        this.eventManager = new EventManager(this);
        this.scoreboardManager = new ScoreboardManager(this);
        this.tablistManager = new TablistManager(this);
        this.databaseManager = new DatabaseManager(this);
        this.statsManager = new StatsManager(this, this.databaseManager);
        this.playerProgressionManager = new PlayerProgressionManager();
        this.bountyManager = new BountyManager(3, 5);
        this.npcManager = new NpcManager(this);
        this.npcAnimationManager = new NpcAnimationManager(this, this.npcManager);
        this.npcAnimationManager.start();
        this.reconnectManager = new ReconnectManager(this);

        // Enregistrement des commandes
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        CommandManager commandManager = new CommandManager(this);
        getCommand("bedwars").setExecutor(commandManager);
        getCommand("bedwars").setTabCompleter(commandManager);
        getCommand("spawn").setExecutor(commandManager);
        getCommand("hub").setExecutor(commandManager);

        registerListeners();

        setupEconomy();
        getLogger().info("HeneriaBedwars a été activé avec succès.");
    }

    @Override
    public void onDisable() {
        if (statsManager != null) {
            statsManager.saveAll();
        }
        if (npcAnimationManager != null) {
            npcAnimationManager.stop();
        }
        getLogger().info("HeneriaBedwars a été désactivé.");
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
        getServer().getPluginManager().registerEvents(new SetupListener(this.setupManager), this);
        getServer().getPluginManager().registerEvents(new NpcListener(), this);
        getServer().getPluginManager().registerEvents(new StarterItemListener(), this);
        getServer().getPluginManager().registerEvents(new SpecialItemListener(), this);
        getServer().getPluginManager().registerEvents(new SpecialNpcListener(), this);
        getServer().getPluginManager().registerEvents(new GolemListener(), this);
        getServer().getPluginManager().registerEvents(new TrapListener(), this);
        getServer().getPluginManager().registerEvents(new StatsListener(), this);
        getServer().getPluginManager().registerEvents(new HungerListener(), this);
        getServer().getPluginManager().registerEvents(new VoidKillListener(), this);
        getServer().getPluginManager().registerEvents(new LobbyProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new LobbyVoidListener(), this);
        getServer().getPluginManager().registerEvents(new TeamSelectorListener(), this);
        getServer().getPluginManager().registerEvents(new LeaveItemListener(), this);
        getServer().getPluginManager().registerEvents(new HealerMilkListener(), this);
        getServer().getPluginManager().registerEvents(new TemperedGlassListener(), this);
        getServer().getPluginManager().registerEvents(new MainLobbyListener(), this);
        getServer().getPluginManager().registerEvents(new ReconnectListener(), this);
        getServer().getPluginManager().registerEvents(new JoinQuitMessageListener(), this);
        getServer().getPluginManager().registerEvents(new PvpListener(), this);
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

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public SpecialShopManager getSpecialShopManager() {
        return specialShopManager;
    }

    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public TablistManager getTablistManager() {
        return tablistManager;
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

    public static NamespacedKey getNpcKey() {
        return npcKey;
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        }
    }

    public Economy getEconomy() {
        return economy;
    }

    public PlayerProgressionManager getPlayerProgressionManager() {
        return playerProgressionManager;
    }

    public BountyManager getBountyManager() {
        return bountyManager;
    }

    public NpcManager getNpcManager() {
        return npcManager;
    }

    public ReconnectManager getReconnectManager() {
        return reconnectManager;
    }

    public NpcAnimationManager getNpcAnimationManager() {
        return npcAnimationManager;
    }

    public Location getMainLobby() {
        return mainLobby;
    }

    public void setMainLobby(Location location) {
        this.mainLobby = location;
        getConfig().set("main-lobby", location);
        saveConfig();
    }
}
