package org.me.oneyz.offerX.utils;

import org.bukkit.ChatColor;

public class ColorUtil {

    public static String translate(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}
