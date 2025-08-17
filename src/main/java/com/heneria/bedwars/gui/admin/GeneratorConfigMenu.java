package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.enums.GeneratorType;
import com.heneria.bedwars.gui.PaginatedMenu;
import com.heneria.bedwars.setup.SetupAction;
import com.heneria.bedwars.setup.SetupType;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Menu for configuring resource generators with pagination.
 */
public class GeneratorConfigMenu extends PaginatedMenu {

    private final Arena arena;
    private final Map<Integer, Generator> generatorSlots = new HashMap<>();
    private List<Generator> generators = new ArrayList<>();

    private static final int IRON_SLOT = 10;
    private static final int GOLD_SLOT = 12;
    private static final int DIAMOND_SLOT = 14;
    private static final int EMERALD_SLOT = 16;

    public GeneratorConfigMenu(Arena arena) {
        this.arena = arena;
    }

    @Override
    public String getTitle() {
        return "Générateurs";
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    protected int getItemStartSlot() {
        return 18;
    }

    @Override
    protected int getItemsPerPage() {
        return 9;
    }

    @Override
    protected int getPrevButtonSlot() {
        return 0;
    }

    @Override
    protected int getNextButtonSlot() {
        return 8;
    }

    @Override
    protected List<ItemStack> getPaginatedItems() {
        generators = new ArrayList<>(arena.getGenerators());
        List<ItemStack> items = new ArrayList<>();
        for (Generator gen : generators) {
            Material mat = materialFromType(gen.getType());
            ItemStack item = new ItemBuilder(mat)
                    .setName("&eGénérateur " + gen.getType().name())
                    .addLore("&cCliquez pour supprimer")
                    .build();
            items.add(item);
        }
        return items;
    }

    @Override
    protected void onItemSet(int index, int slot) {
        generatorSlots.put(slot, generators.get(index));
    }

    @Override
    protected void handleMenuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        if (slot == IRON_SLOT) {
            startSetup(player, GeneratorType.IRON);
        } else if (slot == GOLD_SLOT) {
            startSetup(player, GeneratorType.GOLD);
        } else if (slot == DIAMOND_SLOT) {
            startSetup(player, GeneratorType.DIAMOND);
        } else if (slot == EMERALD_SLOT) {
            startSetup(player, GeneratorType.EMERALD);
        } else if (generatorSlots.containsKey(slot)) {
            arena.getGenerators().remove(generatorSlots.get(slot));
            HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
            MessageUtils.sendMessage(player, "&cGénérateur supprimé.");
            generatorSlots.clear();
            if (page > 0 && page * getItemsPerPage() >= arena.getGenerators().size()) {
                page--;
            }
            setupItems();
        }
    }

    private void startSetup(Player player, GeneratorType type) {
        HeneriaBedwars.getInstance().getSetupManager().startSetup(player,
                new SetupAction(arena, SetupType.GENERATOR, type));
        MessageUtils.sendMessage(player, "&eClic droit pour définir la position du générateur.");
        player.closeInventory();
    }

    @Override
    public void setupItems() {
        generatorSlots.clear();
        super.setupItems();
        inventory.setItem(IRON_SLOT, new ItemBuilder(Material.IRON_INGOT)
                .setName("&fAjouter un générateur de Fer")
                .addLore("&eCliquez pour commencer")
                .build());
        inventory.setItem(GOLD_SLOT, new ItemBuilder(Material.GOLD_INGOT)
                .setName("&fAjouter un générateur d'Or")
                .addLore("&eCliquez pour commencer")
                .build());
        inventory.setItem(DIAMOND_SLOT, new ItemBuilder(Material.DIAMOND)
                .setName("&fAjouter un générateur de Diamant")
                .addLore("&eCliquez pour commencer")
                .build());
        inventory.setItem(EMERALD_SLOT, new ItemBuilder(Material.EMERALD)
                .setName("&fAjouter un générateur d'Émeraude")
                .addLore("&eCliquez pour commencer")
                .build());
    }

    private Material materialFromType(GeneratorType type) {
        return switch (type) {
            case IRON -> Material.IRON_INGOT;
            case GOLD -> Material.GOLD_INGOT;
            case DIAMOND -> Material.DIAMOND;
            case EMERALD -> Material.EMERALD;
        };
    }
}
