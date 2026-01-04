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
    private String cachedServerName;

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
        cachedServerName = connectConfig.readServerName();

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
            String title = config.getOnlineTitle();
            String description = buildOnlineDescription();
            String color = config.getOnlineColor();

            for (String webhookUrl : config.getWebhookUrls()) {
                webhook.sendEmbed(
                        webhookUrl,
                        title,
                        description,
                        color,
                        null,
                        config.getBotUsername(),
                        config.getBotAvatarUrl()
                );
            }

            logger.info("Sent online notification to " + config.getWebhookUrls().size() + " webhook(s)");
        });
    }

    private void sendOfflineNotification() {
        // Run synchronously during shutdown
        String title = config.getOfflineTitle();
        String description = replacePlaceholders(config.getOfflineDescription());
        String color = config.getOfflineColor();

        for (String webhookUrl : config.getWebhookUrls()) {
            webhook.sendEmbed(
                    webhookUrl,
                    title,
                    description,
                    color,
                    null,
                    config.getBotUsername(),
                    config.getBotAvatarUrl()
            );
        }

        logger.info("Sent offline notification to " + config.getWebhookUrls().size() + " webhook(s)");
    }

    private String buildOnlineDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(replacePlaceholders(config.getOnlineDescription()));

        if (config.isShowEndpoint() && cachedEndpoint != null && !cachedEndpoint.equals("unknown")) {
            sb.append("\n\n");
            sb.append(replacePlaceholders(config.getEndpointText()));
        }

        return sb.toString();
    }

    private String replacePlaceholders(String text) {
        if (text == null) return "";

        return text
                .replace("{endpoint}", cachedEndpoint != null ? cachedEndpoint : "unknown")
                .replace("{server-name}", cachedServerName != null ? cachedServerName : "Minecraft Server")
                .replace("{players}", String.valueOf(platform.getOnlinePlayerCount()))
                .replace("{max-players}", String.valueOf(platform.getMaxPlayerCount()));
    }

    public NotifyConfig getConfig() {
        return config;
    }
}

