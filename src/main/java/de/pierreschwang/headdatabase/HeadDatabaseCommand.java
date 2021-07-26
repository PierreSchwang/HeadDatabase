package de.pierreschwang.headdatabase;

import de.pierreschwang.headdatabase.inventory.CategoryInventory;
import de.pierreschwang.headdatabase.task.ReloadTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HeadDatabaseCommand implements CommandExecutor, TabExecutor {

    private final HeadDatabasePlugin plugin;

    protected HeadDatabaseCommand(HeadDatabasePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("rl")) {
            if (!commandSender.hasPermission("headdatabase.admin")) {
                return false;
            }
            if (plugin.getPendingReload().get()) {
                commandSender.sendMessage(plugin.getLanguageHandler().getMessage("pending-reload"));
                return true;
            }
            commandSender.sendMessage(plugin.getLanguageHandler().getMessage("reload-submit"));
            new ReloadTask(plugin, done -> {
                commandSender.sendMessage(plugin.getLanguageHandler().getMessage("reload-done-" + (done ? "success" : "failed")));
            }).runTaskAsynchronously(plugin);
            return true;
        }
        new CategoryInventory(plugin, plugin.getLanguageHandler().getMessage("inventory.category.title"))
                .show((Player) commandSender);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return null;
    }

}