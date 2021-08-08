package de.pierreschwang.headdatabase.inventory;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.pierreschwang.headdatabase.HeadDatabasePlugin;
import de.pierreschwang.headdatabase.dao.Head;
import de.pierreschwang.headdatabase.util.ItemBuilder;
import de.pierreschwang.headdatabase.util.SoundHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchResultInventory extends ChestGui {

    private final HeadDatabasePlugin plugin;
    private final PaginatedPane resultPane = new PaginatedPane(0, 0, 9, 5);

    public SearchResultInventory(HeadDatabasePlugin plugin, String term) {
        super(6, plugin.getLanguageHandler().getMessage("inventory.search.title"));
        this.plugin = plugin;
        // Normalize term
        Set<Head> result = filter(term.trim().toLowerCase());
        if (result.isEmpty()) {
            StaticPane noResultPane = new StaticPane(0, 0, 9, 6);
            noResultPane.addItem(new GuiItem(ItemBuilder
                    .normal(Material.BARRIER)
                    .name(plugin.getLanguageHandler().getMessage("inventory.search.no-result"))
                    .build()
            ), 4, 2);
            addPane(noResultPane);
            return;
        }
        resultPane.populateWithGuiItems(result.stream().map(head -> {
            ItemStack stack = ItemBuilder.wrap(head.toItemStack(plugin))
                    .name(plugin.getLanguageHandler().getMessage(head.isRecent() ? "head.name.recent" : "head.name", head.getName()))
                    .lore(plugin.getLanguageHandler().getMessage("head.lore",
                            plugin.getLanguageHandler().getMessage("category." + head.getCategory().name()),
                            String.join(", ", head.getTags()), String.valueOf(head.getId())
                    )).build();
            return new GuiItem(stack, event -> {
                event.setCancelled(true);
                event.getWhoClicked().getInventory().addItem(stack);
                SoundHelper.playClickSound((Player) event.getWhoClicked());
            });
        }).collect(Collectors.toList()));
        addPane(resultPane);

        StaticPane previousPagePane = new StaticPane(7, 5, 1, 1);
        StaticPane nextPagePane = new StaticPane(8, 5, 1, 1);
        previousPagePane.addItem(new GuiItem(ItemBuilder.normal(Material.ARROW)
                .name(plugin.getLanguageHandler().getMessage("inventory.general.previous-page"))
                .build(), event -> {
            event.setCancelled(true);
            navigate(resultPane.getPage() - 1);
            if (resultPane.getPage() == 0) {
                previousPagePane.setVisible(false);
            }
            nextPagePane.setVisible(true);
            this.update();
        }), 0, 0);
        nextPagePane.addItem(new GuiItem(ItemBuilder.normal(Material.ARROW)
                .name(plugin.getLanguageHandler().getMessage("inventory.general.next-page"))
                .build(), event -> {
            event.setCancelled(true);
            navigate(resultPane.getPage() + 1);
            if (resultPane.getPage() == resultPane.getPages() - 1) {
                nextPagePane.setVisible(false);
            }
            previousPagePane.setVisible(true);
            this.update();
        }), 0, 0);
        previousPagePane.setVisible(false);
        if (resultPane.getPages() < 1) {
            nextPagePane.setVisible(false);
        }
        addPane(previousPagePane);
        addPane(nextPagePane);
    }

    private void navigate(int page) {
        if (page < 0 || page > resultPane.getPages()) {
            return;
        }
        resultPane.setPage(page);
    }

    private Set<Head> filter(String term) {
        Set<Head> found = new HashSet<>();
        // Search by term
        outer:
        for (Head head : plugin.getStorage().getHeads()) {
            if (head.getName().toLowerCase().contains(term)) {
                found.add(head);
                continue;
            }
            for (String tag : head.getTags()) {
                if (tag.toLowerCase().contains(term)) {
                    found.add(head);
                    continue outer;
                }
            }
        }
        return found;
    }

}