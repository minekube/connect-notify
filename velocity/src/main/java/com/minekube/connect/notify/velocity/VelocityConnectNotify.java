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
    private final java.util.logging.Logger logger;

    private ConnectNotify connectNotify;

    @Inject
    public VelocityConnectNotify(ProxyServer proxy, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = proxy;
        this.slf4jLogger = logger;
        this.dataDirectory = dataDirectory;
        this.logger = java.util.logging.Logger.getLogger("ConnectNotify");

        // Bridge SLF4J to java.util.logging
        this.logger.setUseParentHandlers(false);
        this.logger.addHandler(new Slf4jBridgeHandler(logger));
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
        return logger;
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
     * Simple handler to bridge java.util.logging to SLF4J.
     */
    private static class Slf4jBridgeHandler extends java.util.logging.Handler {
        private final Logger slf4jLogger;

        public Slf4jBridgeHandler(Logger slf4jLogger) {
            this.slf4jLogger = slf4jLogger;
        }

        @Override
        public void publish(java.util.logging.LogRecord record) {
            String message = record.getMessage();
            if (record.getLevel().intValue() >= java.util.logging.Level.SEVERE.intValue()) {
                slf4jLogger.error(message);
            } else if (record.getLevel().intValue() >= java.util.logging.Level.WARNING.intValue()) {
                slf4jLogger.warn(message);
            } else if (record.getLevel().intValue() >= java.util.logging.Level.INFO.intValue()) {
                slf4jLogger.info(message);
            } else {
                slf4jLogger.debug(message);
            }
        }

        @Override
        public void flush() {}

        @Override
        public void close() {}
    }
}

