package org.me.oneyz.offerX;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.me.oneyz.offerX.commands.Trade;
import org.me.oneyz.offerX.events.TradeListener;
import org.me.oneyz.offerX.managers.TradeManager;

public final class OfferX extends JavaPlugin {

    private TradeManager tradeManager;

    @Override
    public void onEnable() {
        tradeManager = new TradeManager();

        getCommand("trade").setExecutor(new Trade(tradeManager));

        Bukkit.getPluginManager().registerEvents(new TradeListener(tradeManager), this);

        getLogger().info("OfferX has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("OfferX has been disabled!");
    }

    public TradeManager getTradeManager() {
        return tradeManager;
    }
}