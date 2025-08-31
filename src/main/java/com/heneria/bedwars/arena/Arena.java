package com.heneria.bedwars.arena;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.elements.Team;
import com.heneria.bedwars.arena.enums.GameState;
import com.heneria.bedwars.arena.enums.TeamColor;
import com.heneria.bedwars.arena.enums.GeneratorType;
import com.heneria.bedwars.events.GameStateChangeEvent;
import com.heneria.bedwars.utils.GameUtils;
import com.heneria.bedwars.utils.MessageManager;
import com.heneria.bedwars.managers.StatsManager;
import com.heneria.bedwars.stats.PlayerStats;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.GameMode;
import com.heneria.bedwars.listeners.TeamSelectorListener;
import com.heneria.bedwars.listeners.LeaveItemListener;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EnderDragon;
import org.bukkit.World;
import org.bukkit.GameRule;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Represents a BedWars arena with its configuration and runtime state.
 */
public class Arena {

    private final String name;
    private GameState state;
    private String worldName;
    private boolean enabled;
    private int minPlayers;
    private int maxPlayers;
    private final List<UUID> players = new ArrayList<>();
    private final List<UUID> alivePlayers = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();
    private final Map<TeamColor, Team> teams = new EnumMap<>(TeamColor.class);
    private final List<Generator> generators = new ArrayList<>();
    private Location lobbyLocation;
    private Location specialNpcLocation;
    private Entity specialNpc;
    /**
     * Stores all NPC entities spawned for this arena so they can be removed
     * without affecting NPCs of other arenas.
     */
    private final List<Entity> liveNpcs = new ArrayList<>();
    private final Map<UUID, PlayerData> savedStates = new HashMap<>();
    // NEW CACHE SYSTEM: SIMPLE AND DIRECT
    private final Map<Block, Team> bedBlocks = new HashMap<>();
    private final Map<Block, BlockState> originalBedStates = new HashMap<>();
    private final List<Block> placedBlocks = new ArrayList<>();
    private final List<EnderDragon> dragons = new ArrayList<>();
    /** Tracks per-player purchase counts for limited special shop items. */
    private final Map<UUID, Map<String, Integer>> purchaseCounts = new HashMap<>();
    private BukkitTask countdownTask;
    private int countdownDuration = 10;
    private int countdownTime;
    private int maxBuildY = 256;
    private int maxBuildDistance = 0;

    /**
     * Creates a new arena with the given name.
     *
     * @param name the arena name
     */
    public Arena(String name) {
        this.name = name;
        this.state = GameState.WAITING;
    }

    /**
     * Gets the arena name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the current state of the arena.
     *
     * @return the game state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Sets the current state of the arena.
     *
     * @param state the new state
     */
    public void setState(GameState state) {
        this.state = state;
    }

    /**
     * Checks whether the arena is enabled for play.
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the arena is enabled for play.
     *
     * @param enabled new enabled state
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the name of the world used by the arena.
     *
     * @return the world name
     */
    public String getWorldName() {
        return worldName;
    }

    /**
     * Sets the world used by the arena.
     *
     * @param worldName the world name
     */
    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    /**
     * Gets the minimum number of players required to start the arena.
     *
     * @return minimum player count
     */
    public int getMinPlayers() {
        return minPlayers;
    }

    /**
     * Sets the minimum number of players required.
     *
     * @param minPlayers minimum player count
     */
    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    /**
     * Gets the maximum number of players allowed in the arena.
     *
     * @return maximum player count
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Sets the maximum number of players allowed in the arena.
     *
     * @param maxPlayers maximum player count
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Determines whether a new player can join the arena.
     *
     * @return true if the arena is waiting and not full
     */
    public boolean canJoin() {
        return state == GameState.WAITING && players.size() < maxPlayers;
    }

    public int getMaxBuildY() {
        return maxBuildY;
    }

    public void setMaxBuildY(int maxBuildY) {
        this.maxBuildY = maxBuildY;
    }

    public int getMaxBuildDistance() {
        return maxBuildDistance;
    }

    public void setMaxBuildDistance(int maxBuildDistance) {
        this.maxBuildDistance = maxBuildDistance;
    }

    public int getCountdownTime() {
        return countdownTime;
    }

    /**
     * Gets the list of players currently in the arena.
     *
     * @return list of players
     */
    public List<UUID> getPlayers() {
        return players;
    }

    /**
     * Adds a player to the arena.
     *
     * @param uuid player's unique id
     */
    public void addPlayer(UUID uuid) {
        players.add(uuid);
        alivePlayers.add(uuid);
    }

    /**
     * Removes a player from the arena.
     *
     * @param uuid player's unique id
     */
    public void removePlayer(UUID uuid) {
        players.remove(uuid);
        alivePlayers.remove(uuid);
        spectators.remove(uuid);
    }

    /**
     * Adds a player to this arena and moves them to the lobby.
     *
     * @param player the player to add
     */
    public void addPlayer(Player player) {
        addPlayer(player.getUniqueId());
        savedStates.put(player.getUniqueId(), new PlayerData(player));
        player.getInventory().clear();
        player.teleport(lobbyLocation);
        player.setLevel(0);
        player.setExp(0f);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().setItem(0, TeamSelectorListener.createSelectorItem());
        player.getInventory().setItem(8, LeaveItemListener.createLeaveItem());
        Team team = getLeastPopulatedTeam();
        if (team != null) {
            team.addMember(player.getUniqueId());
            MessageManager.sendMessage(player, "game.team-joined", "team", team.getColor().getDisplayName());
        }
        broadcast("game.player-join-arena", "player", player.getName(), "current_players", String.valueOf(players.size()), "max_players", String.valueOf(maxPlayers));
        HeneriaBedwars.getInstance().getScoreboardManager().setScoreboard(player);
        HeneriaBedwars.getInstance().getTablistManager().updatePlayer(player);
        HeneriaBedwars.getInstance().getPlayerProgressionManager().initPlayer(player.getUniqueId());
        if (players.size() >= minPlayers && state == GameState.WAITING) {
            startCountdown();
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(player)) {
                continue;
            }
            Arena other = HeneriaBedwars.getInstance().getArenaManager().getArena(online);
            if (other != this) {
                online.hidePlayer(HeneriaBedwars.getInstance(), player);
                player.hidePlayer(HeneriaBedwars.getInstance(), online);
            } else {
                online.showPlayer(HeneriaBedwars.getInstance(), player);
                player.showPlayer(HeneriaBedwars.getInstance(), online);
            }
        }
    }

    /**
     * Removes a player from this arena and restores their previous state.
     *
     * @param player the player to remove
     */
    public void removePlayer(Player player) {
        removePlayer(player.getUniqueId());
        Team team = getTeam(player.getUniqueId());
        if (team != null) {
            team.removeMember(player.getUniqueId());
        }
        PlayerData data = savedStates.remove(player.getUniqueId());
        if (data != null) {
            data.restore(player);
        }
        player.setLevel(0);
        player.setExp(0f);
        broadcast("game.player-leave-arena", "player", player.getName());
        HeneriaBedwars.getInstance().getScoreboardManager().setScoreboard(player);
        HeneriaBedwars.getInstance().getTablistManager().updatePlayer(player);
        HeneriaBedwars.getInstance().getPlayerProgressionManager().removePlayer(player.getUniqueId());
        if (state == GameState.STARTING && players.size() < minPlayers) {
            cancelCountdown();
        }
        // Always send players to the main lobby when leaving an arena
        Location lobby = HeneriaBedwars.getInstance().getMainLobby();
        if (lobby != null) {
            player.teleport(lobby);
            player.setGameMode(GameMode.ADVENTURE);
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(player)) {
                continue;
            }
            Arena other = HeneriaBedwars.getInstance().getArenaManager().getArena(online);
            if (other == null) {
                online.showPlayer(HeneriaBedwars.getInstance(), player);
                player.showPlayer(HeneriaBedwars.getInstance(), online);
            } else {
                online.hidePlayer(HeneriaBedwars.getInstance(), player);
                player.hidePlayer(HeneriaBedwars.getInstance(), online);
            }
        }
    }

    public Team getTeam(UUID uuid) {
        for (Team team : teams.values()) {
            if (team.isMember(uuid)) {
                return team;
            }
        }
        return null;
    }

    public Team getTeam(Player player) {
        return getTeam(player.getUniqueId());
    }

    // Bed cache management
    public void registerBed(Block block, Team team) {
        bedBlocks.put(block, team);
    }

    public Team getTeamOfBed(Block block) {
        return bedBlocks.get(block);
    }

    public void clearBeds() {
        bedBlocks.clear();
    }

    // Player placed blocks
    public List<Block> getPlacedBlocks() {
        return placedBlocks;
    }

    public List<EnderDragon> getDragons() {
        return dragons;
    }

    /**
     * Spawns a dragon at the arena's center location.
     */
    public void spawnDragon() {
        Location center = getCenterLocation();
        if (center == null) {
            HeneriaBedwars.getInstance().getLogger().warning("Cannot spawn dragon: center is null for arena " + name);
            return;
        }
        EnderDragon dragon = center.getWorld().spawn(center, EnderDragon.class);
        dragons.add(dragon);
        HeneriaBedwars.getInstance().getLogger().info("Arena " + name + " spawned a dragon at " + center);
    }

    /**
     * Checks and records a purchase for a limited special shop item.
     *
     * @param uuid    player unique id
     * @param itemId  unique item identifier
     * @param limit   maximum times the item may be purchased by this player
     * @return {@code true} if the player can purchase, {@code false} otherwise
     */
    public boolean canPurchase(UUID uuid, String itemId, int limit) {
        if (limit <= 0) {
            return true;
        }
        Map<String, Integer> map = purchaseCounts.computeIfAbsent(uuid, k -> new HashMap<>());
        int count = map.getOrDefault(itemId, 0);
        if (count >= limit) {
            return false;
        }
        map.put(itemId, count + 1);
        return true;
    }

    public void addNpc(Entity entity) {
        liveNpcs.add(entity);
    }

    public Location getSpecialNpcLocation() {
        return specialNpcLocation;
    }

    public void setSpecialNpcLocation(Location specialNpcLocation) {
        this.specialNpcLocation = specialNpcLocation;
    }

    public void spawnSpecialNpc() {
        if (specialNpcLocation == null || specialNpc != null) {
            return;
        }
        ArmorStand npc = (ArmorStand) specialNpcLocation.getWorld().spawnEntity(specialNpcLocation, EntityType.ARMOR_STAND);
        npc.setInvisible(true);
        npc.setInvulnerable(true);
        npc.setGravity(false);
        npc.setBasePlate(false);
        npc.setArms(true);
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Villager"));
        head.setItemMeta(meta);
        npc.getEquipment().setHelmet(head);
        npc.getEquipment().setItemInMainHand(new ItemStack(Material.BLAZE_ROD));
        npc.setCustomName(ChatColor.translateAlternateColorCodes('&', "&5Marchand Mystérieux"));
        npc.setCustomNameVisible(true);
        npc.getPersistentDataContainer().set(HeneriaBedwars.getNpcKey(), PersistentDataType.STRING, "special");
        liveNpcs.add(npc);
        specialNpc = npc;
    }

    public void despawnSpecialNpc() {
        if (specialNpc != null) {
            specialNpc.remove();
            liveNpcs.remove(specialNpc);
            specialNpc = null;
        }
    }

    public void registerBeds() {
        bedBlocks.clear();
        originalBedStates.clear();
        for (Team team : teams.values()) {
            Location headLocation = team.getBedLocation();
            if (headLocation != null) {
                Block headBlock = headLocation.getBlock();
                if (headBlock.getBlockData() instanceof Bed) {
                    Bed bedData = (Bed) headBlock.getBlockData();
                    if (bedData.getPart() != Bed.Part.HEAD) {
                        System.out.println("[ERREUR CRITIQUE] La position sauvegardée pour le lit de l'équipe " + team.getColor() + " n'est pas la tête !");
                        continue;
                    }
                    Block footBlock = headBlock.getRelative(bedData.getFacing().getOppositeFace());
                    bedBlocks.put(headBlock, team);
                    bedBlocks.put(footBlock, team);
                    originalBedStates.put(headBlock, headBlock.getState());
                    originalBedStates.put(footBlock, footBlock.getState());
                    System.out.println("[DEBUG] Lit de " + team.getColor() + " enregistré. Tête: " + headBlock.getLocation().toVector() + ", Pieds: " + footBlock.getLocation().toVector());
                } else {
                    System.out.println("[ERREUR CRITIQUE] Le bloc à la position sauvegardée pour le lit de l'équipe " + team.getColor() + " n'est pas un lit !");
                }
            }
        }
    }

    public void destroyAllBeds() {
        for (Team team : teams.values()) {
            if (team.hasBed()) {
                Location headLocation = team.getBedLocation();
                if (headLocation != null) {
                    Block headBlock = headLocation.getBlock();
                    if (headBlock.getBlockData() instanceof Bed bedData) {
                        Block footBlock = headBlock.getRelative(bedData.getFacing().getOppositeFace());
                        headBlock.setType(Material.AIR);
                        footBlock.setType(Material.AIR);
                    } else {
                        headBlock.setType(Material.AIR);
                    }
                }
                team.setHasBed(false);
            }
        }
        clearBeds();
    }

    public Location getCenterLocation() {
        org.bukkit.World world = Bukkit.getWorld(this.worldName);
        if (world == null) {
            return null;
        }
        double x = 0;
        double z = 0;
        int count = 0;
        for (Team team : teams.values()) {
            Location bed = team.getBedLocation();
            if (bed != null) {
                x += bed.getX();
                z += bed.getZ();
                count++;
            }
        }
        if (count > 0) {
            x /= count;
            z /= count;
        }
        double y = world.getHighestBlockYAt((int) Math.round(x), (int) Math.round(z)) + 10;
        return new Location(world, x, y, z);
    }

    private Team getLeastPopulatedTeam() {
        Team result = null;
        int count = Integer.MAX_VALUE;
        for (Team team : teams.values()) {
            if (team.getMembers().size() < count) {
                count = team.getMembers().size();
                result = team;
            }
        }
        return result;
    }

    public void broadcast(String path, String... placeholders) {
        for (UUID id : players) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                MessageManager.sendMessage(p, path, placeholders);
            }
        }
    }

    public void broadcastTitle(String titlePath, String subtitlePath, int fadeIn, int stay, int fadeOut, String... placeholders) {
        for (UUID id : players) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                MessageManager.sendTitle(p, titlePath, subtitlePath, fadeIn, stay, fadeOut, placeholders);
            }
        }
    }

    public void eliminatePlayer(Player player) {
        removeAlivePlayer(player.getUniqueId());
        addSpectator(player.getUniqueId());
        broadcast("game.player-eliminated", "player", player.getName());
    }

    public List<UUID> getAlivePlayers() {
        return alivePlayers;
    }

    public List<UUID> getSpectators() {
        return spectators;
    }

    public void addSpectator(UUID uuid) {
        spectators.add(uuid);
    }

    public void removeSpectator(UUID uuid) {
        spectators.remove(uuid);
    }

    public void addAlivePlayer(UUID uuid) {
        alivePlayers.add(uuid);
    }

    public void removeAlivePlayer(UUID uuid) {
        alivePlayers.remove(uuid);
    }

    private void startCountdown() {
        state = GameState.STARTING;
        countdownTime = countdownDuration;
        final int total = countdownDuration;
        countdownTask = new BukkitRunnable() {

            @Override
            public void run() {
                System.out.println("[DEBUG-COMPTEUR] Tick ! Temps restant: " + countdownTime);

                if (countdownTime > 0) {
                    broadcast("game.countdown", "time", String.valueOf(countdownTime));
                    for (UUID id : players) {
                        Player p = Bukkit.getPlayer(id);
                        if (p != null) {
                            p.setLevel(countdownTime);
                            p.setExp((float) countdownTime / total);
                            MessageManager.sendTitle(p, "game.countdown-title", "game.countdown-subtitle", 0, 20, 0, "time", String.valueOf(countdownTime));
                        }
                    }
                    countdownTime--;
                } else {
                    System.out.println("[DEBUG-COMPTEUR] Temps écoulé. Annulation du compteur.");
                    this.cancel();

                    System.out.println("[DEBUG-COMPTEUR] APPEL DE ARENA.STARTGAME().");
                    startGame();
                    System.out.println("[DEBUG-COMPTEUR] APPEL DE ARENA.STARTGAME() TERMINÉ.");
                }
            }
        }.runTaskTimer(HeneriaBedwars.getInstance(), 0L, 20L);
    }

    private void cancelCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        state = GameState.WAITING;
        countdownTime = 0;
        for (UUID id : players) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                p.setExp(0f);
                p.setLevel(0);
            }
        }
        broadcast("game.countdown-cancelled");
    }

    /**
     * Starts the game for all players currently in the arena.
     */
    public void startGame() {
        System.out.println("[DEBUG-STARTGAME] La méthode startGame a été appelée pour l'arène " + this.name);

        state = GameState.PLAYING;
        countdownTime = 0;
        System.out.println("[DEBUG-STARTGAME] État passé à PLAYING.");
        Bukkit.getPluginManager().callEvent(new GameStateChangeEvent(this, GameState.PLAYING));

        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
            System.out.println("[DEBUG-STARTGAME] Compte à rebours annulé.");
        }

        World world = Bukkit.getWorld(this.worldName);
        if (world != null) {
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setStorm(false);
            world.setThundering(false);
            world.setTime(6000L);
        }
        System.out.println("[DEBUG-STARTGAME] Préparation du monde terminée.");

        for (UUID id : players) {
            Player p = Bukkit.getPlayer(id);
            if (p == null) {
                continue;
            }
            Team team = getTeam(id);
            if (team != null && team.getSpawnLocation() != null) {
                p.teleport(team.getSpawnLocation());
            }
            p.setGameMode(GameMode.SURVIVAL);
            GameUtils.giveDefaultKit(p, team);
        }
        System.out.println("[DEBUG-STARTGAME] Téléportation des joueurs terminée.");

        for (Generator gen : generators) {
            if (gen.getType() == GeneratorType.DIAMOND || gen.getType() == GeneratorType.EMERALD) {
                Location loc = gen.getLocation();
                if (loc != null) {
                    Location holoLoc = loc.clone().add(0.5, 2.0, 0.5);
                    String title = gen.getType() == GeneratorType.DIAMOND ? ChatColor.AQUA + "Diamant" : ChatColor.GREEN + "Émeraude";
                    int seconds = HeneriaBedwars.getInstance().getGeneratorManager().getDelaySeconds(gen);
                    HeneriaBedwars.getInstance().getHologramManager().createHologram(holoLoc, Arrays.asList(title, seconds + "s"));
                }
            }
            HeneriaBedwars.getInstance().getGeneratorManager().registerGenerator(gen);
        }
        System.out.println("[DEBUG-STARTGAME] Démarrage des générateurs terminé.");

        HeneriaBedwars.getInstance().getEventManager().startTimeline(this);
        System.out.println("[DEBUG-STARTGAME] Démarrage du GameTimer terminé.");

        for (Team team : this.getTeams().values()) {
            if (team.getItemShopNpcLocation() != null) {
                ArmorStand npc = (ArmorStand) team.getItemShopNpcLocation().getWorld().spawnEntity(team.getItemShopNpcLocation(), EntityType.ARMOR_STAND);
                npc.setInvisible(true);
                npc.setInvulnerable(true);
                npc.setGravity(false);
                npc.setBasePlate(false);
                npc.setArms(true);
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Villager"));
                head.setItemMeta(meta);
                npc.getEquipment().setHelmet(head);
                npc.getEquipment().setItemInMainHand(new ItemStack(Material.EMERALD));
                npc.setCustomName(MessageManager.get("game.shop-npc-name"));
                npc.setCustomNameVisible(true);
                npc.getPersistentDataContainer().set(HeneriaBedwars.getNpcKey(), PersistentDataType.STRING,
                        "ITEM_SHOP:" + team.getColor().name());
                equipNpcArmor(npc, team.getItemShopChestplate(), team.getItemShopLeggings(), team.getItemShopBoots(), team.getColor());
                liveNpcs.add(npc);
            }
            if (team.getUpgradeShopNpcLocation() != null) {
                ArmorStand npc = (ArmorStand) team.getUpgradeShopNpcLocation().getWorld().spawnEntity(team.getUpgradeShopNpcLocation(), EntityType.ARMOR_STAND);
                npc.setInvisible(true);
                npc.setInvulnerable(true);
                npc.setGravity(false);
                npc.setBasePlate(false);
                npc.setArms(true);
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Villager"));
                head.setItemMeta(meta);
                npc.getEquipment().setHelmet(head);
                npc.getEquipment().setItemInMainHand(new ItemStack(Material.NETHER_STAR));
                npc.setCustomName(MessageManager.get("game.upgrade-npc-name"));
                npc.setCustomNameVisible(true);
                npc.getPersistentDataContainer().set(HeneriaBedwars.getNpcKey(), PersistentDataType.STRING,
                        "UPGRADE_SHOP:" + team.getColor().name());
                equipNpcArmor(npc, team.getUpgradeShopChestplate(), team.getUpgradeShopLeggings(), team.getUpgradeShopBoots(), team.getColor());
                liveNpcs.add(npc);
            }
        }
        System.out.println("[DEBUG-STARTGAME] Apparition des PNJ terminée.");

        System.out.println("[DEBUG-STARTGAME] La méthode startGame a terminé son exécution SANS ERREUR.");
    }

    private void equipNpcArmor(ArmorStand npc, Material chestplate, Material leggings, Material boots, TeamColor color) {
        if (chestplate != null) {
            ItemStack item = new ItemStack(chestplate);
            tintLeather(item, color);
            npc.getEquipment().setChestplate(item);
        }
        if (leggings != null) {
            ItemStack item = new ItemStack(leggings);
            tintLeather(item, color);
            npc.getEquipment().setLeggings(item);
        }
        if (boots != null) {
            ItemStack item = new ItemStack(boots);
            tintLeather(item, color);
            npc.getEquipment().setBoots(item);
        }
    }

    private void tintLeather(ItemStack item, TeamColor color) {
        if (item.getType().name().startsWith("LEATHER_")) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(color.getLeatherColor());
            item.setItemMeta(meta);
        }
    }

    /**
     * Gets the teams registered in the arena.
     *
     * @return map of teams by color
     */
    public Map<TeamColor, Team> getTeams() {
        return teams;
    }

    /**
     * Gets the generators available in the arena.
     *
     * @return list of generators
     */
    public List<Generator> getGenerators() {
        return generators;
    }

    /**
     * Gets the lobby spawn location of the arena.
     *
     * @return lobby location
     */
    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    /**
     * Sets the lobby spawn location of the arena.
     *
     * @param lobbyLocation lobby location
     */
    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
    }

    public void checkForWinner() {
        Set<Team> teamsAlive = new HashSet<>();
        for (UUID playerUUID : alivePlayers) {
            Player p = Bukkit.getPlayer(playerUUID);
            if (p != null) {
                Team team = getTeam(p);
                if (team != null) {
                    teamsAlive.add(team);
                }
            }
        }

        if (teamsAlive.size() == 1) {
            Team winner = teamsAlive.iterator().next();
            endGame(winner);
        } else if (teamsAlive.isEmpty()) {
            endGame(null); // Gérer le cas d'un match nul ou bug
        }
    }

    public void endGame(Team winner) {
        if (state != GameState.PLAYING) {
            return;
        }
        state = GameState.ENDING;
        StatsManager statsManager = HeneriaBedwars.getInstance().getStatsManager();
        if (winner != null) {
            String teamName = winner.getColor().getChatColor() + winner.getColor().getDisplayName() + ChatColor.RESET;
            broadcast("game.team-win", "team", teamName);
            broadcastTitle("game.win-title", "game.win-subtitle", 10, 70, 20, "team", teamName);
            for (UUID uuid : players) {
                PlayerStats stats = statsManager.getStats(uuid);
                if (stats != null) {
                    stats.incrementGamesPlayed();
                    if (winner.getMembers().contains(uuid)) {
                        stats.incrementWins();
                    } else {
                        stats.incrementLosses();
                    }
                }
            }
        } else {
            broadcast("game.no-winner");
            broadcastTitle("game.no-winner-title", "game.no-winner-subtitle", 10, 70, 20);
            for (UUID uuid : players) {
                PlayerStats stats = statsManager.getStats(uuid);
                if (stats != null) {
                    stats.incrementGamesPlayed();
                    stats.incrementLosses();
                }
            }
        }
        for (Generator gen : generators) {
            HeneriaBedwars.getInstance().getGeneratorManager().unregisterGenerator(gen);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                reset();
            }
        }.runTaskLater(HeneriaBedwars.getInstance(), 200L);
    }

    public void reset() {
        HeneriaBedwars.getInstance().getEventManager().stopTimeline(this);
        for (Generator gen : generators) {
            if (gen.getType() == GeneratorType.DIAMOND || gen.getType() == GeneratorType.EMERALD) {
                Location loc = gen.getLocation();
                if (loc != null) {
                    Location holoLoc = loc.clone().add(0.5, 2.0, 0.5);
                    HeneriaBedwars.getInstance().getHologramManager().removeHologram(holoLoc);
                }
            }
        }
        for (UUID id : new ArrayList<>(players)) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                PlayerData data = savedStates.remove(id);
                if (data != null) {
                    data.restore(p);
                }
                Location mainLobby = HeneriaBedwars.getInstance().getMainLobby();
                if (mainLobby != null) {
                    p.teleport(mainLobby);
                }
                HeneriaBedwars.getInstance().getScoreboardManager().setScoreboard(p);
                // Restore visibility with all other players
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (other.equals(p)) {
                        continue;
                    }
                    p.showPlayer(HeneriaBedwars.getInstance(), other);
                    other.showPlayer(HeneriaBedwars.getInstance(), p);
                }
            }
        }
        players.clear();
        alivePlayers.clear();
        spectators.clear();
        savedStates.clear();
        for (Team team : teams.values()) {
            team.getMembers().clear();
            team.setHasBed(true);
            team.resetUpgrades();
        }
        state = GameState.WAITING;
        liveNpcs.forEach(Entity::remove);
        liveNpcs.clear();
        specialNpc = null;
        dragons.forEach(Entity::remove);
        dragons.clear();
        purchaseCounts.clear();
        HeneriaBedwars.getInstance().getGeneratorManager().resetGenerators(this);
        if (!placedBlocks.isEmpty()) {
            HeneriaBedwars.getInstance().getLogger().info("[DEBUG] Clearing " + placedBlocks.size() + " placed blocks in arena " + name);
            for (Block block : placedBlocks) {
                HeneriaBedwars.getInstance().getLogger().info("[DEBUG] Reset block at " + block.getLocation());
                block.setType(Material.AIR);
            }
            placedBlocks.clear();
            HeneriaBedwars.getInstance().getLogger().info("[DEBUG] Placed blocks list cleared for arena " + name);
        }

        for (BlockState state : originalBedStates.values()) {
            state.update(true, false);
        }
        bedBlocks.clear();
        originalBedStates.clear();

        World world = Bukkit.getWorld(this.worldName);
        if (world != null) {
            for (Entity entity : new ArrayList<>(world.getEntities())) {
                if (entity.getType() == EntityType.ITEM) {
                    entity.remove();
                }
            }
        }
    }
}
