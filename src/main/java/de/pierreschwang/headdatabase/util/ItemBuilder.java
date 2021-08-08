package de.pierreschwang.headdatabase.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder<T extends ItemMeta> {

    private final ItemStack itemStack;
    private final T meta;

    public static ItemBuilder<ItemMeta> normal(Material material) {
        return new ItemBuilder<>(new ItemStack(material), Bukkit.getItemFactory().getItemMeta(material));
    }

    public static ItemBuilder<ItemMeta> wrap(ItemStack base) {
        return new ItemBuilder<>(base, base.getItemMeta());
    }

    public static ItemBuilder<ItemMeta> clone(ItemStack base) {
        return wrap(base.clone());
    }

    ItemBuilder(ItemStack itemStack, T meta) {
        this.itemStack = itemStack;
        this.meta = meta;
    }

    public ItemBuilder<T> name(String name) {
        this.meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    public ItemBuilder<T> lore(List<String> lore) {
        this.meta.setLore(lore);
        return this;
    }

    public ItemBuilder<T> lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public ItemBuilder<T> lore(String lore) {
        return lore(lore.split("\n"));
    }

    public ItemBuilder<T> flags(ItemFlag... flags) {
        this.meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder<T> enchant(Enchantment enchantment, int level) {
        this.meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(meta);
        return this.itemStack;
    }

}
