package de.pierreschwang.headdatabase.inventory;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.pierreschwang.headdatabase.HeadDatabasePlugin;
import de.pierreschwang.headdatabase.dao.Category;
import de.pierreschwang.headdatabase.util.ItemBuilder;
import de.pierreschwang.headdatabase.util.SoundHelper;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CategoryInventory extends ChestGui {

    private final HeadDatabasePlugin plugin;
    private final StaticPane categoryPane = new StaticPane(2, 1, 5, 2);

    public CategoryInventory(HeadDatabasePlugin plugin, @NotNull String title) {
        super(4, title);
        this.plugin = plugin;
        setCategories();
        setBackground();
    }

    private void setBackground() {
        ItemStack pane = ItemBuilder.normal(Material.GRAY_STAINED_GLASS_PANE).name("§r").build();
        StaticPane background = new StaticPane(0, 0, 9, 4, Pane.Priority.LOWEST);
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < getRows(); y++) {
                background.addItem(new GuiItem(pane, event -> event.setCancelled(true)), x, y);
            }
        }
        addPane(background);
    }

    private void setCategories() {
        int index = 0;
        for (Category value : Category.values()) {
            int x = index >= 5 ? index - 5 : index;
            int y = index < 5 ? 0 : 1;
            ItemStack stack = value.toItemStack(plugin.getLanguageHandler().getMessage("category." + value.name()));
            categoryPane.addItem(new GuiItem(stack, event -> {
                event.setCancelled(true);
                SoundHelper.playClickSound((Player) event.getWhoClicked());
                new CategorySkullsInventory(plugin, value, ChatColor.stripColor(plugin.getLanguageHandler().getMessage("category." + value.name())))
                        .show(event.getWhoClicked());
            }), x, y);
            index++;
        }
        addPane(categoryPane);
    }

}
