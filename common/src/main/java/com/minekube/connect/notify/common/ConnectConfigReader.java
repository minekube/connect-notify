package com.minekube.connect.notify.common;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Reads the Minekube Connect plugin configuration to get the endpoint.
 */
public class ConnectConfigReader {

    private static final String CONNECT_CONFIG_PATH = "connect/config.yml";

    private final File pluginsFolder;
    private final Logger logger;

    public ConnectConfigReader(File pluginsFolder, Logger logger) {
        this.pluginsFolder = pluginsFolder;
        this.logger = logger;
    }

    /**
     * Reads the endpoint from Minekube Connect's configuration.
     *
     * @return The endpoint string, or "unknown" if not found
     */
    public String readEndpoint() {
        File configFile = new File(pluginsFolder, CONNECT_CONFIG_PATH);
        if (configFile.exists()) {
            String endpoint = readEndpointFromFile(configFile);
            if (endpoint != null && !endpoint.isEmpty()) {
                return endpoint;
            }
        }

        logger.warning("Could not find Minekube Connect configuration at " + configFile.getPath() +
                ". Make sure the Connect plugin is installed and configured.");
        return "unknown";
    }

    @SuppressWarnings("unchecked")
    private String readEndpointFromFile(File file) {
        try (InputStream in = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(in);
            if (config == null) {
                return null;
            }

            // Try to find endpoint in various locations
            // Check root level
            if (config.containsKey("endpoint")) {
                Object endpoint = config.get("endpoint");
                if (endpoint instanceof String) {
                    return (String) endpoint;
                }
            }

            // Check under 'connect' section
            if (config.containsKey("connect")) {
                Object connectSection = config.get("connect");
                if (connectSection instanceof Map) {
                    Map<String, Object> connect = (Map<String, Object>) connectSection;
                    if (connect.containsKey("endpoint")) {
                        Object endpoint = connect.get("endpoint");
                        if (endpoint instanceof String) {
                            return (String) endpoint;
                        }
                    }
                }
            }

            // Check for 'name' which might be used as endpoint
            if (config.containsKey("name")) {
                Object name = config.get("name");
                if (name instanceof String) {
                    return (String) name;
                }
            }

            logger.fine("Connect config found at " + file.getPath() + " but no endpoint field found");
        } catch (Exception e) {
            logger.warning("Failed to read Connect config at " + file.getPath() + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Reads the server name from Connect's configuration.
     *
     * @return The server name, or "Minecraft Server" if not found
     */
    public String readServerName() {
        File configFile = new File(pluginsFolder, CONNECT_CONFIG_PATH);
        if (configFile.exists()) {
            String name = readServerNameFromFile(configFile);
            if (name != null && !name.isEmpty()) {
                return name;
            }
        }
        return "Minecraft Server";
    }

    @SuppressWarnings("unchecked")
    private String readServerNameFromFile(File file) {
        try (InputStream in = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(in);
            if (config == null) {
                return null;
            }

            // Check for server name
            if (config.containsKey("server-name")) {
                Object name = config.get("server-name");
                if (name instanceof String) {
                    return (String) name;
                }
            }

            if (config.containsKey("serverName")) {
                Object name = config.get("serverName");
                if (name instanceof String) {
                    return (String) name;
                }
            }

        } catch (Exception e) {
            // Ignore, not critical
        }
        return null;
    }
}

