package com.minekube.connect.notify.common;

import java.util.logging.Logger;

/**
 * Core logic for Connect Notify, shared across all platforms.
 */
public class ConnectNotify {

    private final ConnectNotifyPlatform platform;
    private final NotifyConfig config;
    private final ConnectConfigReader connectConfig;
    private final DiscordWebhook webhook;
    private final Logger logger;

    private String cachedEndpoint;

    public ConnectNotify(ConnectNotifyPlatform platform) {
        this.platform = platform;
        this.logger = platform.getLogger();
        this.config = new NotifyConfig(platform.getDataFolder(), logger);
        this.connectConfig = new ConnectConfigReader(platform.getPluginsFolder(), logger);
        this.webhook = new DiscordWebhook(logger);
    }

    /**
     * Called when the plugin is enabled / server starts.
     */
    public void onEnable() {
        config.load();

        if (!config.hasWebhooks()) {
            logger.warning("No Discord webhooks configured! Please add webhook URLs to config.yml");
            return;
        }

        // Cache Connect endpoint
        cachedEndpoint = connectConfig.readEndpoint();

        logger.info("Connect Notify enabled on " + platform.getPlatformName());
        logger.info("Endpoint: " + cachedEndpoint);

        // Send online notification
        if (config.isOnlineEnabled()) {
            sendOnlineNotification();
        }
    }

    /**
     * Called when the plugin is disabled / server stops.
     */
    public void onDisable() {
        if (!config.hasWebhooks()) {
            return;
        }

        // Send offline notification (synchronously since server is shutting down)
        if (config.isOfflineEnabled()) {
            sendOfflineNotification();
        }

        logger.info("Connect Notify disabled");
    }

    private void sendOnlineNotification() {
        platform.runAsync(() -> {
            for (String webhookUrl : config.getWebhookUrls()) {
                webhook.sendOnlineEmbed(
                        webhookUrl,
                        cachedEndpoint,
                        platform.getOnlinePlayerCount(),
                        platform.getMaxPlayerCount(),
                        config.getBotUsername(),
                        config.getBotAvatarUrl()
                );
            }

            logger.info("Sent online notification to " + config.getWebhookUrls().size() + " webhook(s)");
        });
    }

    private void sendOfflineNotification() {
        // Run synchronously during shutdown
        for (String webhookUrl : config.getWebhookUrls()) {
            webhook.sendOfflineEmbed(
                    webhookUrl,
                    cachedEndpoint,
                    config.getBotUsername(),
                    config.getBotAvatarUrl()
            );
        }

        logger.info("Sent offline notification to " + config.getWebhookUrls().size() + " webhook(s)");
    }

    public NotifyConfig getConfig() {
        return config;
    }
}
