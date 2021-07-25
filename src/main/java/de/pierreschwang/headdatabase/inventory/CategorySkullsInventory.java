package de.pierreschwang.headdatabase.inventory;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import de.pierreschwang.headdatabase.HeadDatabasePlugin;
import de.pierreschwang.headdatabase.dao.Category;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class CategorySkullsInventory extends ChestGui {

    private final HeadDatabasePlugin plugin;
    private PaginatedPane skullPane = new PaginatedPane(9, 5);

    public CategorySkullsInventory(HeadDatabasePlugin plugin, Category category, @NotNull String title) {
        super(6, title);
        this.plugin = plugin;
        skullPane.populateWithGuiItems(plugin.getStorage().getHeadsByCategory().get(category).stream().map(head -> {
            ItemStack stack = head.toItemStack(plugin);
            return new GuiItem(stack, event -> {
                event.setCancelled(true);
                event.getWhoClicked().getInventory().addItem(stack);
            });
        }).collect(Collectors.toList()));
        addPane(skullPane);
    }

}