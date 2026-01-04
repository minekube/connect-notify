package com.minekube.connect.notify.velocity;

import com.google.inject.Inject;
import com.minekube.connect.notify.common.ConnectNotify;
import com.minekube.connect.notify.common.ConnectNotifyPlatform;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Velocity implementation of Connect Notify.
 */
@Plugin(
        id = "connect-notify",
        name = "ConnectNotify",
        version = "@version@",
        description = "Discord notifications for Minekube Connect server status",
        authors = {"Minekube"},
        url = "https://connect.minekube.com",
        dependencies = {
                @Dependency(id = "connect", optional = true)
        }
)
public class VelocityConnectNotify implements ConnectNotifyPlatform {

    private final ProxyServer proxy;
    private final Logger slf4jLogger;
    private final Path dataDirectory;
    private final java.util.logging.Logger julLogger;

    private ConnectNotify connectNotify;

    @Inject
    public VelocityConnectNotify(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.slf4jLogger = logger;
        this.dataDirectory = dataDirectory;
        
        // Create a custom JUL logger that bridges to SLF4J
        this.julLogger = new Slf4jLoggingAdapter(logger);
    }

    @Subscribe(order = PostOrder.LATE)
    public void onProxyInit(ProxyInitializeEvent event) {
        connectNotify = new ConnectNotify(this);
        connectNotify.onEnable();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (connectNotify != null) {
            connectNotify.onDisable();
        }
    }

    @Override
    public String getPlatformName() {
        return "Velocity";
    }

    @Override
    public File getDataFolder() {
        return dataDirectory.toFile();
    }

    @Override
    public java.util.logging.Logger getLogger() {
        return julLogger;
    }

    @Override
    public void runAsync(Runnable task) {
        proxy.getScheduler()
                .buildTask(this, task)
                .delay(0, TimeUnit.MILLISECONDS)
                .schedule();
    }

    @Override
    public int getOnlinePlayerCount() {
        return proxy.getPlayerCount();
    }

    @Override
    public int getMaxPlayerCount() {
        return proxy.getConfiguration().getShowMaxPlayers();
    }

    /**
     * A java.util.logging.Logger adapter that forwards all log calls to SLF4J.
     */
    private static class Slf4jLoggingAdapter extends java.util.logging.Logger {
        private final Logger slf4jLogger;

        public Slf4jLoggingAdapter(Logger slf4jLogger) {
            super("ConnectNotify", null);
            this.slf4jLogger = slf4jLogger;
            setLevel(Level.ALL);
        }

        @Override
        public void log(LogRecord record) {
            String message = record.getMessage();
            if (message == null) return;
            
            int level = record.getLevel().intValue();
            if (level >= Level.SEVERE.intValue()) {
                slf4jLogger.error(message);
            } else if (level >= Level.WARNING.intValue()) {
                slf4jLogger.warn(message);
            } else if (level >= Level.INFO.intValue()) {
                slf4jLogger.info(message);
            } else {
                slf4jLogger.debug(message);
            }
        }

        @Override
        public void info(String msg) {
            slf4jLogger.info(msg);
        }

        @Override
        public void warning(String msg) {
            slf4jLogger.warn(msg);
        }

        @Override
        public void severe(String msg) {
            slf4jLogger.error(msg);
        }

        @Override
        public void fine(String msg) {
            slf4jLogger.debug(msg);
        }

        @Override
        public void finer(String msg) {
            slf4jLogger.trace(msg);
        }

        @Override
        public void finest(String msg) {
            slf4jLogger.trace(msg);
        }

        @Override
        public boolean isLoggable(Level level) {
            return true;
        }
    }
}
