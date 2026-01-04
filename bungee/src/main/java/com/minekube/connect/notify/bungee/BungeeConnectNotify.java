package com.minekube.connect.notify.bungee;

import com.minekube.connect.notify.common.ConnectNotify;
import com.minekube.connect.notify.common.ConnectNotifyPlatform;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * BungeeCord implementation of Connect Notify.
 */
public class BungeeConnectNotify extends Plugin implements ConnectNotifyPlatform {

    private ConnectNotify connectNotify;

    @Override
    public void onEnable() {
        connectNotify = new ConnectNotify(this);
        connectNotify.onEnable();
    }

    @Override
    public void onDisable() {
        if (connectNotify != null) {
            connectNotify.onDisable();
        }
    }

    @Override
    public String getPlatformName() {
        return "BungeeCord";
    }

    // getDataFolder() and getLogger() are inherited from Plugin

    @Override
    public void runAsync(Runnable task) {
        ProxyServer.getInstance().getScheduler().schedule(this, task, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public int getOnlinePlayerCount() {
        return ProxyServer.getInstance().getOnlineCount();
    }

    @Override
    public int getMaxPlayerCount() {
        return ProxyServer.getInstance().getConfig().getPlayerLimit();
    }
}

