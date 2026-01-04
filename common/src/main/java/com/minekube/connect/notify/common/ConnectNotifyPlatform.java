package com.minekube.connect.notify.common;

import java.io.File;
import java.util.logging.Logger;

/**
 * Platform abstraction for Connect Notify.
 * Each platform (Bukkit, Velocity, BungeeCord) implements this interface.
 */
public interface ConnectNotifyPlatform {

    /**
     * Gets the platform name (e.g., "Paper", "Velocity", "BungeeCord")
     */
    String getPlatformName();

    /**
     * Gets the data folder for this plugin's configuration
     */
    File getDataFolder();

    /**
     * Gets the logger for this plugin
     */
    Logger getLogger();

    /**
     * Gets the plugins folder (parent of data folder)
     */
    default File getPluginsFolder() {
        return getDataFolder().getParentFile();
    }

    /**
     * Runs a task asynchronously
     */
    void runAsync(Runnable task);

    /**
     * Gets the current online player count
     */
    int getOnlinePlayerCount();

    /**
     * Gets the maximum player count
     */
    int getMaxPlayerCount();
}

