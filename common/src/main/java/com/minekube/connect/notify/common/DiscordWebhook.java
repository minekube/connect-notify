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

    private static final String MINEKUBE_ICON = "https://github.com/minekube.png";
    private static final String MINEKUBE_URL = "https://connect.minekube.com";

    private final Logger logger;

    public DiscordWebhook(Logger logger) {
        this.logger = logger;
    }

    /**
     * Sends a server online notification.
     */
    public void sendOnlineEmbed(String webhookUrl, String endpoint, int players, int maxPlayers,
                                 String username, String avatarUrl) {
        try {
            JsonObject payload = createBasePayload(username, avatarUrl);
            JsonObject embed = new JsonObject();

            // Author with icon and clickable link
            JsonObject author = new JsonObject();
            author.addProperty("name", "Server Online");
            author.addProperty("icon_url", MINEKUBE_ICON);
            author.addProperty("url", MINEKUBE_URL);
            embed.add("author", author);

            // Description with address
            embed.addProperty("description", "🎮 Join now and start playing!\n```\n" + endpoint + "\n```");

            // Green color
            embed.addProperty("color", 0x57F287);

            // Inline fields for width
            JsonArray fields = new JsonArray();

            JsonObject statusField = new JsonObject();
            statusField.addProperty("name", "Status");
            statusField.addProperty("value", "🟢 Online");
            statusField.addProperty("inline", true);
            fields.add(statusField);

            JsonObject playersField = new JsonObject();
            playersField.addProperty("name", "Players");
            playersField.addProperty("value", players + "/" + maxPlayers);
            playersField.addProperty("inline", true);
            fields.add(playersField);

            JsonObject pingField = new JsonObject();
            pingField.addProperty("name", "Ping");
            pingField.addProperty("value", "Ready");
            pingField.addProperty("inline", true);
            fields.add(pingField);

            embed.add("fields", fields);

            // Footer with link
            JsonObject footer = new JsonObject();
            footer.addProperty("text", "Minekube Connect • connect.minekube.com");
            embed.add("footer", footer);

            // Timestamp
            embed.addProperty("timestamp", java.time.Instant.now().toString());

            // Add embed to payload
            JsonArray embeds = new JsonArray();
            embeds.add(embed);
            payload.add("embeds", embeds);

            sendRequest(webhookUrl, payload.toString());

        } catch (Exception e) {
            logger.warning("Failed to send Discord webhook: " + e.getMessage());
        }
    }

    /**
     * Sends a server offline notification.
     */
    public void sendOfflineEmbed(String webhookUrl, String endpoint, String username, String avatarUrl) {
        try {
            JsonObject payload = createBasePayload(username, avatarUrl);
            JsonObject embed = new JsonObject();

            // Author with icon and clickable link
            JsonObject author = new JsonObject();
            author.addProperty("name", "Server Offline");
            author.addProperty("icon_url", MINEKUBE_ICON);
            author.addProperty("url", MINEKUBE_URL);
            embed.add("author", author);

            // Description with address
            embed.addProperty("description", "🎮 See you next time!\n```\n" + endpoint + "\n```");

            // Red color
            embed.addProperty("color", 0xED4245);

            // Inline fields for width
            JsonArray fields = new JsonArray();

            JsonObject statusField = new JsonObject();
            statusField.addProperty("name", "Status");
            statusField.addProperty("value", "🔴 Offline");
            statusField.addProperty("inline", true);
            fields.add(statusField);

            JsonObject playersField = new JsonObject();
            playersField.addProperty("name", "Players");
            playersField.addProperty("value", "—");
            playersField.addProperty("inline", true);
            fields.add(playersField);

            JsonObject pingField = new JsonObject();
            pingField.addProperty("name", "Ping");
            pingField.addProperty("value", "—");
            pingField.addProperty("inline", true);
            fields.add(pingField);

            embed.add("fields", fields);

            // Footer with link
            JsonObject footer = new JsonObject();
            footer.addProperty("text", "Minekube Connect • connect.minekube.com");
            embed.add("footer", footer);

            // Timestamp
            embed.addProperty("timestamp", java.time.Instant.now().toString());

            // Add embed to payload
            JsonArray embeds = new JsonArray();
            embeds.add(embed);
            payload.add("embeds", embeds);

            sendRequest(webhookUrl, payload.toString());

        } catch (Exception e) {
            logger.warning("Failed to send Discord webhook: " + e.getMessage());
        }
    }

    /**
     * Creates the base webhook payload with username and avatar.
     */
    private JsonObject createBasePayload(String username, String avatarUrl) {
        JsonObject payload = new JsonObject();

        if (username != null && !username.isEmpty()) {
            payload.addProperty("username", username);
        }
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            payload.addProperty("avatar_url", avatarUrl);
        }

        return payload;
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
}
