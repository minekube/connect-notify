package com.minekube.connect.notify.common;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Configuration handler for Connect Notify plugin.
 */
public class NotifyConfig {

    private final File configFile;
    private final Logger logger;

    // Discord settings
    private List<String> webhookUrls = new ArrayList<>();
    private String botUsername = "Connect Notify";
    private String botAvatarUrl = "https://connect.minekube.com/img/logo.png";

    // Online message settings
    private boolean onlineEnabled = true;
    private String onlineTitle = "Server Online! 🟢";
    private String onlineDescription = "The server is now up and running.";
    private String onlineColor = "#00ff00";
    private boolean showEndpoint = true;
    private String endpointText = "Connect with: `{endpoint}`";

    // Offline message settings
    private boolean offlineEnabled = true;
    private String offlineTitle = "Server Offline 🔴";
    private String offlineDescription = "The server has been shut down.";
    private String offlineColor = "#ff0000";

    public NotifyConfig(File dataFolder, Logger logger) {
        this.configFile = new File(dataFolder, "config.yml");
        this.logger = logger;
    }

    public void load() {
        if (!configFile.exists()) {
            saveDefault();
        }

        try (InputStream in = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(in);
            if (config == null) {
                config = new LinkedHashMap<>();
            }

            // Load discord section
            Map<String, Object> discord = getMap(config, "discord");
            Object webhooksObj = discord.get("webhooks");
            if (webhooksObj instanceof List) {
                webhookUrls = new ArrayList<>();
                for (Object url : (List<?>) webhooksObj) {
                    if (url instanceof String && !((String) url).isEmpty() 
                            && !((String) url).equals("https://discord.com/api/webhooks/...")) {
                        webhookUrls.add((String) url);
                    }
                }
            }
            botUsername = getString(discord, "username", botUsername);
            botAvatarUrl = getString(discord, "avatar-url", botAvatarUrl);

            // Load messages section
            Map<String, Object> messages = getMap(config, "messages");

            // Online message
            Map<String, Object> online = getMap(messages, "online");
            onlineEnabled = getBoolean(online, "enabled", onlineEnabled);
            onlineTitle = getString(online, "title", onlineTitle);
            onlineDescription = getString(online, "description", onlineDescription);
            onlineColor = getString(online, "color", onlineColor);
            showEndpoint = getBoolean(online, "show-endpoint", showEndpoint);
            endpointText = getString(online, "endpoint-text", endpointText);

            // Offline message
            Map<String, Object> offline = getMap(messages, "offline");
            offlineEnabled = getBoolean(offline, "enabled", offlineEnabled);
            offlineTitle = getString(offline, "title", offlineTitle);
            offlineDescription = getString(offline, "description", offlineDescription);
            offlineColor = getString(offline, "color", offlineColor);

        } catch (Exception e) {
            logger.severe("Failed to load config: " + e.getMessage());
        }
    }

    private void saveDefault() {
        configFile.getParentFile().mkdirs();
        try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
            if (in != null) {
                Files.copy(in, configFile.toPath());
            } else {
                // Create default config manually if resource not found
                createDefaultConfig();
            }
        } catch (IOException e) {
            logger.severe("Failed to save default config: " + e.getMessage());
        }
    }

    private void createDefaultConfig() throws IOException {
        String defaultConfig = """
                # Connect Notify Configuration
                # Discord notifications for Minekube Connect server status
                
                # Discord webhook notifications
                discord:
                  # List of webhook URLs to send notifications to
                  # Create one in Discord: Server Settings > Integrations > Webhooks > New Webhook
                  webhooks:
                    - 'https://discord.com/api/webhooks/...'
                    # - 'https://discord.com/api/webhooks/...'  # Add more webhooks here
                
                  # Bot appearance (optional)
                  username: 'Connect Notify'
                  avatar-url: 'https://connect.minekube.com/img/logo.png'
                
                # Message settings
                messages:
                  # Online message - sent when server starts
                  online:
                    enabled: true
                    title: 'Server Online! 🟢'
                    description: 'The server is now up and running.'
                    color: '#00ff00'
                    show-endpoint: true
                    endpoint-text: 'Connect with: `{endpoint}`'
                
                  # Offline message - sent when server stops
                  offline:
                    enabled: true
                    title: 'Server Offline 🔴'
                    description: 'The server has been shut down.'
                    color: '#ff0000'
                """;
        Files.writeString(configFile.toPath(), defaultConfig);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Map<String, Object> parent, String key) {
        Object value = parent.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return new LinkedHashMap<>();
    }

    private String getString(Map<String, Object> map, String key, String def) {
        Object value = map.get(key);
        return value instanceof String ? (String) value : def;
    }

    private boolean getBoolean(Map<String, Object> map, String key, boolean def) {
        Object value = map.get(key);
        return value instanceof Boolean ? (Boolean) value : def;
    }

    // Getters
    public List<String> getWebhookUrls() { return webhookUrls; }
    public String getBotUsername() { return botUsername; }
    public String getBotAvatarUrl() { return botAvatarUrl; }
    public boolean isOnlineEnabled() { return onlineEnabled; }
    public String getOnlineTitle() { return onlineTitle; }
    public String getOnlineDescription() { return onlineDescription; }
    public String getOnlineColor() { return onlineColor; }
    public boolean isShowEndpoint() { return showEndpoint; }
    public String getEndpointText() { return endpointText; }
    public boolean isOfflineEnabled() { return offlineEnabled; }
    public String getOfflineTitle() { return offlineTitle; }
    public String getOfflineDescription() { return offlineDescription; }
    public String getOfflineColor() { return offlineColor; }

    public boolean hasWebhooks() {
        return !webhookUrls.isEmpty();
    }
}

