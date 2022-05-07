package de.pierreschwang.headdatabase.http;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import com.google.gson.Gson;
import de.pierreschwang.headdatabase.HeadDatabasePlugin;
import de.pierreschwang.headdatabase.dao.Category;
import de.pierreschwang.headdatabase.dao.Head;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HeadDatabaseResolver {

    private static final String SOURCE_URL = "https://minecraft-heads.4lima.de/csv/2022-02-25-ZgFDreHnLiGvHdf3RFfgg/Custom-Head-DB.csv";
    private static final int MAX_TRIES = 10;

    private final HeadDatabasePlugin plugin;
    private final Set<Head> heads = new HashSet<>();

    public HeadDatabaseResolver(HeadDatabasePlugin plugin) {
        this.plugin = plugin;
    }

    public void downloadDatabase(Consumer<Boolean> consumer) {
        downloadDatabase(1, consumer);
    }

    /**
     * Download the heads from minecraft-heads.com and update the local database
     *
     * @param tryCount The current try to download the heads
     */
    private void downloadDatabase(int tryCount, Consumer<Boolean> consumer) {
        // If max tries passed, disable ourself
        if (tryCount > MAX_TRIES) {
            plugin.getLogger().severe("Failed to download heads - " + (tryCount - 1) + " unsuccessful tries");
            consumer.accept(false);
            return;
        }
        plugin.getLogger().info("Trying to download heads [Try " + tryCount + "/" + MAX_TRIES + "]");

        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) new URL(SOURCE_URL).openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(10));
            connection.connect();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.contains(";")) {
                        continue;
                    }
                    // Syntax: category;id;name;texture;recent (1, otherwise 0);tags (seperated by pipes)
                    List<String> data = Arrays.stream(line.split(";"))
                            .map(s -> s.replace("\"", "").replace('-', '_'))
                            .collect(Collectors.toList());
                    if (data.size() < 5) {
                        continue;
                    }
                    Category category = Category.byKey(data.get(0));
                    int id = Integer.parseInt(data.get(1));
                    String name = data.get(2);
                    String texture = data.get(3);
                    boolean recent = data.get(4).equals("1");
                    Set<String> tags = data.size() < 6 ?
                            ImmutableSet.of() :
                            Arrays.stream(data.get(5).split("\\|")).collect(Collectors.toSet());
                    heads.add(new Head(id, category, name, texture, recent, tags));
                }
            }
            Files.write(new Gson().toJson(heads).getBytes(StandardCharsets.UTF_8), plugin.getStorage().getLocalDatabase());
            plugin.getLogger().info("Updated local database");
            consumer.accept(true);
        } catch (IOException e) {
            plugin.getLogger().warning("Try " + tryCount + " to download heads failed");
            e.printStackTrace();
            downloadDatabase(tryCount + 1, consumer);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    public Set<Head> getHeads() {
        return heads;
    }

}