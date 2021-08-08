package de.pierreschwang.headdatabase;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import de.pierreschwang.headdatabase.api.AvatarApi;
import de.pierreschwang.headdatabase.commands.HeadDatabaseCommand;
import de.pierreschwang.headdatabase.commands.PlayerHeadCommand;
import de.pierreschwang.headdatabase.i18n.LanguageHandler;
import de.pierreschwang.headdatabase.impl.McHeadsAvatarApi;
import de.pierreschwang.headdatabase.storage.HeadStorage;
import de.pierreschwang.headdatabase.task.ReloadTask;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class HeadDatabasePlugin extends JavaPlugin {

    private LanguageHandler languageHandler;
    private HeadStorage storage;

    private BukkitAudiences bukkitAudiences;
    private BukkitCommandManager<CommandSender> commandManager;

    private AvatarApi avatarApi;

    private final AtomicBoolean pendingReload = new AtomicBoolean(false);

    @Override
    public void onLoad() {
        getDataFolder().mkdirs();
        avatarApi = new McHeadsAvatarApi(this);
    }

    @Override
    public void onEnable() {
        try {
            LanguageHandler languageHandler = new LanguageHandler(this);
            setLanguageHandler(languageHandler);
        } catch (IOException e) {
            getLogger().severe("An error occurred while initializing the language properties");
            e.printStackTrace();
        }

        try {
            bukkitAudiences = BukkitAudiences.create(this);
            commandManager = new BukkitCommandManager<>(
                    this, CommandExecutionCoordinator.simpleCoordinator(), Function.identity(), Function.identity()
            );
            HeadDatabaseCommandExceptionHandler.apply(languageHandler, commandManager, sender -> bukkitAudiences.sender(sender));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.storage = new HeadStorage(this);
        new ReloadTask(this).runTaskAsynchronously(this);
        AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(
                commandManager, CommandSender.class, parameters -> SimpleCommandMeta.empty()
        );
        annotationParser.parse(new PlayerHeadCommand(this));
        annotationParser.parse(new HeadDatabaseCommand(this));
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

    public BukkitAudiences getBukkitAudiences() {
        return bukkitAudiences;
    }

    public AvatarApi getAvatarApi() {
        return avatarApi;
    }
}