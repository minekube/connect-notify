package com.minekube.connect.notify.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * Discord webhook message sender with rich embed support.
 */
public class DiscordWebhook {

    private final Logger logger;

    public DiscordWebhook(Logger logger) {
        this.logger = logger;
    }

    /**
     * Sends a rich embed message to a Discord webhook.
     *
     * @param webhookUrl  The Discord webhook URL
     * @param title       The embed title
     * @param description The embed description
     * @param colorHex    The embed color in hex format (e.g., "#00ff00")
     * @param footerText  Optional footer text (can be null)
     * @param username    The bot username (can be null)
     * @param avatarUrl   The bot avatar URL (can be null)
     */
    public void sendEmbed(String webhookUrl, String title, String description,
                          String colorHex, String footerText, String username, String avatarUrl) {
        try {
            JsonObject payload = new JsonObject();

            // Set bot username and avatar if provided
            if (username != null && !username.isEmpty()) {
                payload.addProperty("username", username);
            }
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                payload.addProperty("avatar_url", avatarUrl);
            }

            // Create embed
            JsonObject embed = new JsonObject();
            embed.addProperty("title", title);
            embed.addProperty("description", description);
            embed.addProperty("color", parseColor(colorHex));

            // Add footer if provided
            if (footerText != null && !footerText.isEmpty()) {
                JsonObject footer = new JsonObject();
                footer.addProperty("text", footerText);
                embed.add("footer", footer);
            }

            // Add timestamp
            embed.addProperty("timestamp", java.time.Instant.now().toString());

            // Add embed to array
            JsonArray embeds = new JsonArray();
            embeds.add(embed);
            payload.add("embeds", embeds);

            // Send request
            sendRequest(webhookUrl, payload.toString());

        } catch (Exception e) {
            logger.warning("Failed to send Discord webhook: " + e.getMessage());
        }
    }

    /**
     * Sends a simple text message to a Discord webhook.
     */
    public void sendMessage(String webhookUrl, String content, String username, String avatarUrl) {
        try {
            JsonObject payload = new JsonObject();
            payload.addProperty("content", content);

            if (username != null && !username.isEmpty()) {
                payload.addProperty("username", username);
            }
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                payload.addProperty("avatar_url", avatarUrl);
            }

            sendRequest(webhookUrl, payload.toString());

        } catch (Exception e) {
            logger.warning("Failed to send Discord webhook: " + e.getMessage());
        }
    }

    private void sendRequest(String webhookUrl, String jsonPayload) throws Exception {
        URL url = URI.create(webhookUrl).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "ConnectNotify/1.0");
            connection.setDoOutput(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == 204 || responseCode == 200) {
                logger.fine("Discord webhook sent successfully");
            } else if (responseCode == 429) {
                logger.warning("Discord webhook rate limited. Message may be delayed.");
            } else {
                logger.warning("Discord webhook returned status code: " + responseCode);
            }

        } finally {
            connection.disconnect();
        }
    }

    /**
     * Parses a hex color string to Discord's integer color format.
     */
    private int parseColor(String hex) {
        if (hex == null || hex.isEmpty()) {
            return 0x00ff00; // Default green
        }

        // Remove # if present
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }

        try {
            return Integer.parseInt(hex, 16);
        } catch (NumberFormatException e) {
            return 0x00ff00; // Default green
        }
    }
}

