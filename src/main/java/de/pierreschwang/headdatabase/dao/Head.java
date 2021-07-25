package de.pierreschwang.headdatabase.dao;

import de.pierreschwang.headdatabase.HeadDatabasePlugin;
import de.pierreschwang.headdatabase.util.SkullHelper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

public class Head {

    private static final String TEXTURE_OBJECT = "{\"textures\":{\"SKIN\":{\"url\":\"https://textures.minecraft.net/texture/%s\"}}}";

    private final int id;
    private final Category category;
    private final String name;
    private final String texture, texture64;
    private final boolean recent;
    private final Set<String> tags;

    public Head(int id, Category category, String name, String texture, boolean recent, Set<String> tags) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.texture = texture;
        this.texture64 = Base64.getEncoder().encodeToString(String.format(TEXTURE_OBJECT, texture).getBytes(StandardCharsets.UTF_8));
        this.recent = recent;
        this.tags = tags;
    }

    public ItemStack toItemStack(HeadDatabasePlugin plugin) {
        ItemStack stack = SkullHelper.createSkull(texture64);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(plugin.getLanguageHandler().getMessage("head.name", name));
        stack.setItemMeta(meta);
        return stack;
    }

    public String getTexture64() {
        return texture64;
    }

    public int getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public String getTexture() {
        return texture;
    }

    public boolean isRecent() {
        return recent;
    }

    public Set<String> getTags() {
        return tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Head head = (Head) o;

        if (getId() != head.getId()) return false;
        if (isRecent() != head.isRecent()) return false;
        if (getCategory() != head.getCategory()) return false;
        if (!getName().equals(head.getName())) return false;
        if (!getTexture().equals(head.getTexture())) return false;
        return getTags().equals(head.getTags());
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getCategory().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getTexture().hashCode();
        result = 31 * result + (isRecent() ? 1 : 0);
        result = 31 * result + getTags().hashCode();
        return result;
    }

}