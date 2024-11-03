package org.me.oneyz.offerX;

import org.bukkit.plugin.java.JavaPlugin;
import org.me.oneyz.offerX.commands.Trade;

import java.util.Objects;

public final class OfferX extends JavaPlugin {

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("trade")).setExecutor(new Trade());
    }

    @Override
    public void onDisable() {
    }
}
