package de.pierreschwang.headdatabase.commands;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import de.pierreschwang.headdatabase.HeadDatabasePlugin;
import de.pierreschwang.headdatabase.dao.Head;
import de.pierreschwang.headdatabase.util.SkullHelper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerHeadCommand {

    private static final String BASE64_RAW_TEMPLATE = "{\"textures:\"{\"SKIN\":{\"url\":\"%s\"}}}";
    private static final String MOJANG_TEXTUR_BASE = "https://textures.minecraft.net/texture/";
    private static final Pattern MINECRAFT_HEADS_URL = Pattern.compile("(https://)?minecraft-heads\\.com/custom-heads/blocks/(.*)");
    private static final Pattern MOJANG_TEXTURE_URL = Pattern.compile("((http|https)://)?textures\\.minecraft\\.net/texture/([0-9a-fA-F]{64})");
    private static final Pattern UUID_TO_DASHES = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

    private final HeadDatabasePlugin plugin;

    public PlayerHeadCommand(HeadDatabasePlugin plugin) {
        this.plugin = plugin;
    }

    // https://minecraft-heads.com/custom-heads/blocks/46101-cobbled-deepslate
    // PlayerName
    // UUID
    // Base64 Texture - not working yet
    // Mojang Texture URL - not working yet
    @CommandMethod(value = "playerhead|phead <input>", requiredSender = Player.class)
    @CommandPermission("headdatabase.playerhead")
    private void executePlayerHeadCommandSelf(Player sender,
                                              @Argument("input") String input) {
        // Handle minecraft-heads.com urls
        Matcher mcHeadsMatcher = MINECRAFT_HEADS_URL.matcher(input);
        if (mcHeadsMatcher.find()) {
            giveMinecraftHeadByUrl(sender, mcHeadsMatcher.group(2));
            return;
        }

        // Handle player names
        if (input.length() >= 3 && input.length() <= 16) {
            giveMinecraftHeadByName(sender, input);
            return;
        }

        // Handle player uuids
        if (input.length() == 32 || input.length() == 36) {
            try {
                UUID uuid = UUID.fromString(input.length() == 36 ?
                        input :
                        UUID_TO_DASHES.matcher(input).replaceAll("$1-$2-$3-$4-$5")
                );
                giveMinecraftHeadByUuid(sender, uuid);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(plugin.getLanguageHandler().getMessage("invalid-uuid", input));
            }
            return;
        }

        // Handle mojang texture
        /* if (input.length() == 64) {
            giveMinecraftHeadByTexture(sender, MOJANG_TEXTUR_BASE + input);
            return;
        } */

        // Handle mojang texture urls
        /* Matcher mojangMatcher = MOJANG_TEXTURE_URL.matcher(input);
        if (mcHeadsMatcher.find()) {
            giveMinecraftHeadByTexture(sender, mojangMatcher.group(3));
            return;
        } */

        // error handling
        sender.sendMessage(plugin.getLanguageHandler().getMessage("playerhead.invalid-input", input));
    }

    @SuppressWarnings("UnstableApiUsage")
    private void giveMinecraftHeadByName(Player sender, String name) {
        Futures.addCallback(plugin.getAvatarApi().getTexture(name), new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String result) {
                giveMinecraftHeadByTexture(sender, result);
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                sender.sendMessage(plugin.getLanguageHandler().getMessage("playerhead.name.notfound", name));
            }
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    private void giveMinecraftHeadByUuid(Player sender, UUID uuid) {
        Futures.addCallback(plugin.getAvatarApi().getTexture(uuid), new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String result) {
                giveMinecraftHeadByTexture(sender, result);
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
                sender.sendMessage(plugin.getLanguageHandler().getMessage("playerhead.uuid.notfound", uuid));
            }
        });
    }

    private void giveMinecraftHeadByUrl(Player sender, String urlParam) {
        // Only get the id from the url parameter...
        StringBuilder idString = new StringBuilder();
        for (char c : urlParam.toCharArray()) {
            if (c >= 47 && c <= 58) {
                idString.append(c);
                continue;
            }
            break;
        }
        try {
            int id = Integer.parseInt(idString.toString());
            for (Head head : plugin.getStorage().getHeads()) {
                if (head.getId() == id) {
                    sender.getInventory().addItem(head.toItemStack(plugin));
                    return;
                }
            }
            sender.sendMessage(plugin.getLanguageHandler().getMessage("playerhead.notfound.urlParam", idString));
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getLanguageHandler().getMessage("playerhead.invalid.urlParam", idString));
        }
    }

    private void giveMinecraftHeadByTexture(Player sender, String texture) {
        try {
            // is base64
            Base64.getDecoder().decode(texture.getBytes(StandardCharsets.UTF_8));
            sender.getInventory().addItem(SkullHelper.createSkull(texture));
        } catch (IllegalArgumentException e) {
            // no base64
            sender.getInventory().addItem(SkullHelper.createSkull(Base64.getEncoder().encodeToString(
                    String.format(BASE64_RAW_TEMPLATE, texture).getBytes(StandardCharsets.UTF_8)
            )));
        }
    }

}
