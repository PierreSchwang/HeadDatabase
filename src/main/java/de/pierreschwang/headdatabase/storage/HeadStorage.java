package de.pierreschwang.headdatabase.storage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import de.pierreschwang.headdatabase.HeadDatabasePlugin;
import de.pierreschwang.headdatabase.dao.Category;
import de.pierreschwang.headdatabase.dao.Head;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class HeadStorage {

    private Set<Head> heads = new HashSet<>();
    private Map<Category, Set<Head>> headsByCategory = new HashMap<>();
    private final File localDatabase;

    private final HeadDatabasePlugin plugin;

    public HeadStorage(HeadDatabasePlugin plugin) {
        this.plugin = plugin;
        this.localDatabase = new File(plugin.getDataFolder(), "heads.json");
    }

    public void load() throws IOException {
        this.heads = new Gson().fromJson(
                new String(Files.readAllBytes(localDatabase.toPath()), StandardCharsets.UTF_8),
                new TypeToken<Set<Head>>() {
                }.getType());
        for (Head head : heads) {
            headsByCategory.computeIfAbsent(head.getCategory(), c -> new HashSet<>()).add(head);
        }
    }

    public Set<Head> getHeads() {
        return heads;
    }

    public Map<Category, Set<Head>> getHeadsByCategory() {
        return headsByCategory;
    }

    public File getLocalDatabase() {
        return localDatabase;
    }

}
