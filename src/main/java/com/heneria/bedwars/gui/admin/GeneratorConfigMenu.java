package com.heneria.bedwars.gui.admin;

import com.heneria.bedwars.HeneriaBedwars;
import com.heneria.bedwars.arena.Arena;
import com.heneria.bedwars.gui.Menu;
import com.heneria.bedwars.utils.ItemBuilder;
import com.heneria.bedwars.setup.SetupAction;
import com.heneria.bedwars.setup.SetupType;
import com.heneria.bedwars.arena.elements.Generator;
import com.heneria.bedwars.arena.enums.GeneratorType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

/**
 * Menu for configuring resource generators.
 */
public class GeneratorConfigMenu extends Menu {

    private final Arena arena;

    private static final int IRON_SLOT = 10;
    private static final int GOLD_SLOT = 12;
    private static final int DIAMOND_SLOT = 14;
    private static final int EMERALD_SLOT = 16;

    private final Map<Integer, Generator> generatorSlots = new HashMap<>();

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
    public void setupItems() {
        inventory.setItem(IRON_SLOT, new ItemBuilder(Material.IRON_INGOT)
                .setName("Ajouter un générateur de Fer")
                .build());
        inventory.setItem(GOLD_SLOT, new ItemBuilder(Material.GOLD_INGOT)
                .setName("Ajouter un générateur d'Or")
                .build());
        inventory.setItem(DIAMOND_SLOT, new ItemBuilder(Material.DIAMOND)
                .setName("Ajouter un générateur de Diamant")
                .build());
        inventory.setItem(EMERALD_SLOT, new ItemBuilder(Material.EMERALD)
                .setName("Ajouter un générateur d'Émeraude")
                .build());

        int slot = 18;
        for (Generator gen : arena.getGenerators()) {
            Material mat = materialFromType(gen.getType());
            ItemStack item = new ItemBuilder(mat)
                    .setName("Générateur " + gen.getType().name())
                    .addLore("Cliquez pour supprimer")
                    .build();
            inventory.setItem(slot, item);
            generatorSlots.put(slot, gen);
            slot++;
        }
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        int slot = event.getRawSlot();
        if (slot == IRON_SLOT) {
            HeneriaBedwars.getInstance().getSetupManager().startSetup(player,
                    new SetupAction(arena, SetupType.GENERATOR, GeneratorType.IRON));
        } else if (slot == GOLD_SLOT) {
            HeneriaBedwars.getInstance().getSetupManager().startSetup(player,
                    new SetupAction(arena, SetupType.GENERATOR, GeneratorType.GOLD));
        } else if (slot == DIAMOND_SLOT) {
            HeneriaBedwars.getInstance().getSetupManager().startSetup(player,
                    new SetupAction(arena, SetupType.GENERATOR, GeneratorType.DIAMOND));
        } else if (slot == EMERALD_SLOT) {
            HeneriaBedwars.getInstance().getSetupManager().startSetup(player,
                    new SetupAction(arena, SetupType.GENERATOR, GeneratorType.EMERALD));
        } else if (generatorSlots.containsKey(slot)) {
            arena.getGenerators().remove(generatorSlots.get(slot));
            HeneriaBedwars.getInstance().getArenaManager().saveArena(arena);
            player.sendMessage("Générateur supprimé.");
            player.closeInventory();
            new GeneratorConfigMenu(arena).open(player);
            return;
        } else {
            return;
        }
        player.sendMessage("Clic droit pour définir la position du générateur.");
        player.closeInventory();
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
