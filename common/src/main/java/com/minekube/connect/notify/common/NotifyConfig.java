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

    private static final String DEFAULT_ICON = "https://github.com/minekube.png";

    // Discord settings
    private List<String> webhookUrls = new ArrayList<>();
    private String botUsername = "Connect Notify";
    private String botAvatarUrl = DEFAULT_ICON;

    // Notification toggles
    private boolean onlineEnabled = true;
    private boolean offlineEnabled = true;

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
                            && !((String) url).contains("...")) {
                        webhookUrls.add((String) url);
                    }
                }
            }
            botUsername = getString(discord, "username", botUsername);
            botAvatarUrl = getString(discord, "avatar-url", botAvatarUrl);

            // Load notifications section
            Map<String, Object> notifications = getMap(config, "notifications");
            onlineEnabled = getBoolean(notifications, "online", onlineEnabled);
            offlineEnabled = getBoolean(notifications, "offline", offlineEnabled);

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
                createDefaultConfig();
            }
        } catch (IOException e) {
            logger.severe("Failed to save default config: " + e.getMessage());
        }
    }

    private void createDefaultConfig() throws IOException {
        String defaultConfig = """
                # Connect Notify - Discord notifications for your Minecraft server
                # https://github.com/minekube/connect-notify
                
                discord:
                  # Add your Discord webhook URL(s) here
                  # Create one: Right-click channel > Edit Channel > Integrations > Webhooks
                  webhooks:
                    - ''
                
                  # Bot appearance
                  username: 'Connect Notify'
                  avatar-url: 'https://github.com/minekube.png'
                
                # Enable/disable notifications
                notifications:
                  online: true
                  offline: true
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
    public boolean isOfflineEnabled() { return offlineEnabled; }

    public boolean hasWebhooks() {
        return !webhookUrls.isEmpty();
    }
}
