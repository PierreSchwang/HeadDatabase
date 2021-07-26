package de.pierreschwang.headdatabase.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundHelper {

    private static Sound clickSound;

    static {
        for (Sound sound : Sound.values()) {
            if (sound.name().equals("CLICK") || sound.name().equals("UI_BUTTON_CLICK")) {
                clickSound = sound;
            }
        }
    }

    public static void playClickSound(Player player) {
        player.playSound(player.getLocation(), clickSound, 1, 1);
    }

}
