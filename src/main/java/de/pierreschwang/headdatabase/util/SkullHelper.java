package de.pierreschwang.headdatabase.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class SkullHelper {

    private static Field skullMetaProfileField;
    private static Method skullMetaSetProfileMethod;
    private static Material SKULL_MATERIAL;

    static {
        if ((SKULL_MATERIAL = Material.getMaterial("PLAYER_HEAD")) == null) {
            SKULL_MATERIAL = Material.getMaterial("SKULL_ITEM");
        }
    }

    // SKULL_ITEM (old) - PLAYER_HEAD (new)
    public static Material getSkullMaterial() {
        return SKULL_MATERIAL;
    }

    public static ItemStack getSkullStack() {
        if (getSkullMaterial().name().equals("PLAYER_HEAD")) {
            return new ItemStack(getSkullMaterial());
        }
        return new ItemStack(getSkullMaterial(), 1, (short) 3);
    }

    public static ItemStack createSkull(String textureBase64) {
        ItemStack stack = getSkullStack();
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
        gameProfile.getProperties().put("textures", new Property("textures", textureBase64));

        if (skullMetaProfileField == null && skullMetaSetProfileMethod == null) {
            try {
                skullMetaSetProfileMethod = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                skullMetaSetProfileMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                try {
                    skullMetaProfileField = meta.getClass().getDeclaredField("profile");
                    skullMetaProfileField.setAccessible(true);
                } catch (NoSuchFieldException ex) {
                    e.printStackTrace();
                    ex.printStackTrace();
                }
            }
        }
        try {
            if (skullMetaSetProfileMethod != null) {
                skullMetaSetProfileMethod.invoke(meta, gameProfile);
            }
            if (skullMetaProfileField != null) {
                skullMetaProfileField.set(meta, gameProfile);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        stack.setItemMeta(meta);
        return stack;
    }


}
