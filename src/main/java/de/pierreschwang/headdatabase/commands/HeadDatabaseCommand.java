package de.pierreschwang.headdatabase.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.AnvilGui;
import com.github.stefvanschie.inventoryframework.util.version.Version;
import de.pierreschwang.headdatabase.HeadDatabasePlugin;
import de.pierreschwang.headdatabase.inventory.CategoryInventory;
import de.pierreschwang.headdatabase.inventory.SearchResultInventory;
import de.pierreschwang.headdatabase.task.ReloadTask;
import de.pierreschwang.headdatabase.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeadDatabaseCommand {

    private final HeadDatabasePlugin plugin;

    public HeadDatabaseCommand(HeadDatabasePlugin plugin) {
        this.plugin = plugin;
    }

    @CommandMethod(value = "headdatabase|hdb", requiredSender = Player.class)
    @CommandPermission("headdatabase.hdb")
    private void executePlayerHeadDatabaseCommand(Player sender) {
        new CategoryInventory(plugin, plugin.getLanguageHandler().getMessage("inventory.category.title"))
                .show(sender);
    }

    @CommandMethod(value = "headdatabase|hdb search|s [term]", requiredSender = Player.class)
    @CommandPermission("headdatabase.hdb.search")
    private void executePlayerHeadDatabaseSearchCommand(Player sender, @Argument(value = "term") String term) {
        if (term != null && !term.isEmpty()) {
            new SearchResultInventory(plugin, term).show(sender);
            return;
        }
    }

    @CommandMethod(value = "headdatabase|hdb reload|rl")
    @CommandPermission("headdatabase.admin.reload")
    private void executePlayerHeadDatabaseReloadCommand(CommandSender sender) {
        if (plugin.getPendingReload().get()) {
            sender.sendMessage(plugin.getLanguageHandler().getMessage("pending-reload"));
            return;
        }
        sender.sendMessage(plugin.getLanguageHandler().getMessage("reload-submit"));
        new ReloadTask(plugin, done ->
                sender.sendMessage(plugin.getLanguageHandler().getMessage("reload-done-" + (done ? "success" : "failed"))))
                .runTaskAsynchronously(plugin);
    }

}
