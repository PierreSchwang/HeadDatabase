package de.pierreschwang.headdatabase;

import de.pierreschwang.headdatabase.i18n.LanguageHandler;
import de.pierreschwang.headdatabase.storage.HeadStorage;
import de.pierreschwang.headdatabase.task.ReloadTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicBoolean;

public class HeadDatabasePlugin extends JavaPlugin {

    private LanguageHandler languageHandler;
    private HeadStorage storage;

    private final AtomicBoolean pendingReload = new AtomicBoolean(false);

    @Override
    public void onLoad() {
        getDataFolder().mkdirs();
    }

    @Override
    public void onEnable() {
        this.storage = new HeadStorage(this);
        new ReloadTask(this).runTaskAsynchronously(this);
        getCommand("headdatabase").setExecutor(new HeadDatabaseCommand(this));
    }

    @Override
    public void onDisable() {
    }

    public AtomicBoolean getPendingReload() {
        return pendingReload;
    }

    public HeadStorage getStorage() {
        return storage;
    }

    public void setLanguageHandler(LanguageHandler languageHandler) {
        this.languageHandler = languageHandler;
    }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }
}