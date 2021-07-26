package de.pierreschwang.headdatabase.i18n;

import de.pierreschwang.headdatabase.HeadDatabasePlugin;
import org.bukkit.ChatColor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class LanguageHandler {

    private final HeadDatabasePlugin plugin;
    private final File translationFile;

    private final Map<String, String> translations = new HashMap<>();

    public LanguageHandler(HeadDatabasePlugin plugin) throws IOException {
        this.plugin = plugin;
        this.translationFile = new File(plugin.getDataFolder(), "messages.properties");
        Properties userTranslations = mergeTranslations(getTranslationsFromUser());
        loadTranslations(userTranslations);
    }

    public String getMessage(String key, Object... params) {
        return MessageFormat.format(translations.getOrDefault(key, "N/A"), params);
    }

    /**
     * Actual loading of the translations
     */
    private void loadTranslations(Properties userTranslations) {
        String prefix = ChatColor.translateAlternateColorCodes('&', String.valueOf(userTranslations.getOrDefault("prefix", "")));
        for (String key : userTranslations.stringPropertyNames()) {
            String value = ChatColor.translateAlternateColorCodes('&', userTranslations.getProperty(key));
            value = value.replace("%prefix%", prefix);
            translations.put(key, value);
        }
    }

    /**
     * Add new translations into the user file, if new ones were added into lang/messages.properties
     */
    private Properties mergeTranslations(Properties userTranslations) throws IOException {
        boolean fresh = userTranslations.isEmpty();
        // Initial copy of all data
        if (fresh) {
            Files.copy(Objects.requireNonNull(plugin.getResource("lang/messages.properties")), translationFile.toPath());
            plugin.getLogger().info("Messages can be modified at " + translationFile.getAbsolutePath());
            return getTranslationsFromResources();
        }
        // append new locale keys
        Properties resources = getTranslationsFromResources();
        for (String key : resources.stringPropertyNames()) {
            if (userTranslations.containsKey(key)) {
                continue;
            }
            userTranslations.put(key, resources.getProperty(key));
            com.google.common.io.Files.append('\n' + key + "=" + resources.getProperty(key), translationFile, StandardCharsets.UTF_8);
        }
        return userTranslations;
    }

    private Properties getTranslationsFromResources() throws IOException {
        Properties properties = new Properties();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(plugin.getResource("lang/messages.properties")), StandardCharsets.UTF_8))) {
            properties.load(reader);
        }
        return properties;
    }

    private Properties getTranslationsFromUser() throws IOException {
        Properties properties = new Properties();
        if (!translationFile.exists()) {
            return properties;
        }
        try (BufferedReader reader = Files.newBufferedReader(translationFile.toPath(), StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

}