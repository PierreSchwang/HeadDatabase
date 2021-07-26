package de.pierreschwang.headdatabase.task;

import de.pierreschwang.headdatabase.HeadDatabasePlugin;
import de.pierreschwang.headdatabase.http.HeadDatabaseResolver;
import de.pierreschwang.headdatabase.i18n.LanguageHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ReloadTask extends BukkitRunnable {

    private final HeadDatabasePlugin plugin;
    private final Consumer<Boolean> done;

    public ReloadTask(HeadDatabasePlugin plugin) {
        this(plugin, unused -> {
        });
    }

    public ReloadTask(HeadDatabasePlugin plugin, Consumer<Boolean> done) {
        this.plugin = plugin;
        this.done = done;
    }

    @Override
    public void run() {
        plugin.getPendingReload().set(true);
        AtomicBoolean successful = new AtomicBoolean(true);
        try {
            LanguageHandler languageHandler = new LanguageHandler(plugin);
            plugin.setLanguageHandler(languageHandler);
        } catch (IOException e) {
            successful.set(false);
            plugin.getLogger().severe("An error occurred while initializing the language properties");
            e.printStackTrace();
        }
        new HeadDatabaseResolver(plugin).downloadDatabase(result -> {
            if (result) {
                try {
                    plugin.getStorage().load();
                } catch (IOException e) {
                    successful.set(false);
                    plugin.getLogger().severe("An error occurred while reloading the local head storage");
                    e.printStackTrace();
                }
            } else {
                successful.set(false);
            }
        });
        plugin.getPendingReload().set(false);
        done.accept(successful.get());
    }

}