package com.minekube.connect.notify.bukkit;

import com.minekube.connect.notify.common.ConnectNotify;
import com.minekube.connect.notify.common.ConnectNotifyPlatform;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

/**
 * Bukkit/Spigot/Paper implementation of Connect Notify.
 */
public class BukkitConnectNotify extends JavaPlugin implements ConnectNotifyPlatform {

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
        return Bukkit.getName();
    }

    // getDataFolder() and getLogger() are inherited from JavaPlugin

    @Override
    public void runAsync(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(this, task);
    }

    @Override
    public int getOnlinePlayerCount() {
        return Bukkit.getOnlinePlayers().size();
    }

    @Override
    public int getMaxPlayerCount() {
        return Bukkit.getMaxPlayers();
    }
}

