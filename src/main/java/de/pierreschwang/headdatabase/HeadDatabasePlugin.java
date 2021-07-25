package de.pierreschwang.headdatabase;

import de.pierreschwang.headdatabase.http.HeadDatabaseResolver;
import de.pierreschwang.headdatabase.i18n.LanguageHandler;
import de.pierreschwang.headdatabase.storage.HeadStorage;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class HeadDatabasePlugin extends JavaPlugin {

    private LanguageHandler languageHandler;
    private HeadStorage storage;

    @Override
    public void onLoad() {
        languageHandler = new LanguageHandler();
    }

    @Override
    public void onEnable() {
        this.storage = new HeadStorage(this);
        new HeadDatabaseResolver(this).downloadDatabase();
        try {
            this.storage.load();
        } catch (IOException e) {
            getLogger().severe("Failed to load heads");
            e.printStackTrace();
        }
        getCommand("headdatabase").setExecutor(new HeadDatabaseCommand(this));
    }

    @Override
    public void onDisable() {

    }

    public HeadStorage getStorage() {
        return storage;
    }

    public LanguageHandler getLanguageHandler() {
        return languageHandler;
    }
}