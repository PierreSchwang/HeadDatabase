package de.pierreschwang.headdatabase;

import cloud.commandframework.CommandManager;
import cloud.commandframework.exceptions.InvalidCommandSenderException;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import de.pierreschwang.headdatabase.i18n.LanguageHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

import java.util.function.Function;

public class HeadDatabaseCommandExceptionHandler {

    public static void apply(LanguageHandler languageHandler, CommandManager<CommandSender> commandManager, Function<CommandSender, Audience> senderAudienceMapper) {
        new MinecraftExceptionHandler<CommandSender>()
                .withArgumentParsingHandler().withInvalidSyntaxHandler()
                .withHandler(MinecraftExceptionHandler.ExceptionType.INVALID_SENDER, e ->
                        Component.text(languageHandler.getMessage("commands.invalid-sender", ((InvalidCommandSenderException) e).getRequiredSender().getSimpleName())))
                .withHandler(MinecraftExceptionHandler.ExceptionType.NO_PERMISSION, e ->
                        Component.text(languageHandler.getMessage("commands.no-permission")))
                .withCommandExecutionHandler()
                .apply(commandManager, senderAudienceMapper);
    }

}
