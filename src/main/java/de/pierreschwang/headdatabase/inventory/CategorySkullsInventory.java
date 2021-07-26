package de.pierreschwang.headdatabase.inventory;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.pierreschwang.headdatabase.HeadDatabasePlugin;
import de.pierreschwang.headdatabase.dao.Category;
import de.pierreschwang.headdatabase.util.SoundHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class CategorySkullsInventory extends ChestGui {

    private final HeadDatabasePlugin plugin;
    private final PaginatedPane skullPane = new PaginatedPane(9, 5);

    private final StaticPane previousPagePane = new StaticPane(7, 5, 1, 1);
    private final StaticPane nextPagePane = new StaticPane(8, 5, 1, 1);

    public CategorySkullsInventory(HeadDatabasePlugin plugin, Category category, @NotNull String title) {
        super(6, title);
        this.plugin = plugin;
        skullPane.populateWithGuiItems(plugin.getStorage().getHeadsByCategory().get(category).stream().map(head -> {
            ItemStack stack = head.toItemStack(plugin);
            return new GuiItem(stack, event -> {
                event.setCancelled(true);
                event.getWhoClicked().getInventory().addItem(stack);
                SoundHelper.playClickSound((Player) event.getWhoClicked());
            });
        }).collect(Collectors.toList()));
        addPane(skullPane);

        previousPagePane.addItem(new GuiItem(new ItemStack(Material.ARROW), event -> {
            event.setCancelled(true);
            navigate(skullPane.getPage() - 1);
            if (skullPane.getPage() == 0) {
                previousPagePane.setVisible(false);
            }
            nextPagePane.setVisible(true);
            this.update();
        }), 0, 0);
        nextPagePane.addItem(new GuiItem(new ItemStack(Material.ARROW), event -> {
            event.setCancelled(true);
            navigate(skullPane.getPage() + 1);
            if (skullPane.getPage() == skullPane.getPages() - 1) {
                nextPagePane.setVisible(false);
            }
            previousPagePane.setVisible(true);
            this.update();
        }), 0, 0);
        previousPagePane.setVisible(false);
        if (skullPane.getPages() < 1) {
            nextPagePane.setVisible(false);
        }
        addPane(previousPagePane);
        addPane(nextPagePane);
    }

    private void navigate(int page) {
        if (page < 0 || page > skullPane.getPages()) {
            return;
        }
        skullPane.setPage(page);
    }
}