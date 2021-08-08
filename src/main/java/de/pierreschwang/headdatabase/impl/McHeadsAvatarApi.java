package de.pierreschwang.headdatabase.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.pierreschwang.headdatabase.HeadDatabasePlugin;
import de.pierreschwang.headdatabase.api.AvatarApi;
import org.bukkit.Bukkit;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class McHeadsAvatarApi implements AvatarApi {

    private static final String BASE_URL = "https://mc-heads.net/minecraft/profile/";
    private final HeadDatabasePlugin plugin;
    private final JsonParser jsonParser = new JsonParser();

    public McHeadsAvatarApi(HeadDatabasePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public ListenableFuture<String> getTexture(String username) {
        SettableFuture<String> future = SettableFuture.create();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL(BASE_URL + username);
                HttpsURLConnection request = (HttpsURLConnection) url.openConnection();
                request.connect();
                if (request.getResponseCode() == 204) {
                    future.setException(new NullPointerException("No content returned"));
                    return;
                }
                try (InputStreamReader reader = new InputStreamReader(request.getInputStream())) {
                    JsonObject object = jsonParser.parse(reader).getAsJsonObject();
                    future.set(object.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                future.setException(e);
            }
        });
        return future;
    }

    @Override
    public ListenableFuture<String> getTexture(UUID uuid) {
        return getTexture(uuid.toString());
    }

}