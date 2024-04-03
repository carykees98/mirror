package org.lavajuno.mirrorlog.config;

import org.lavajuno.lucidjson.*;
import org.lavajuno.mirrorlog.main.LogMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.*;

/**
 * ApplicationConfig loads and stores the program configuration, and
 * provides functionality for retrieving configuration values.
 */
@SuppressWarnings("unused")
public class ApplicationConfig implements JsonSerializable {
    private static ApplicationConfig instance;

    private int threads;
    private int port;
    private int timeout;
    private int revision;
    private boolean restricted;
    private Set<String> allowed_addresses;
    private int component_pad;
    private boolean log_to_file;
    private int file_duration;
    private int file_history;

    /**
     * Gets the instance of ApplicationConfig. Will create it if it does not already exist.
     * @return Instance of ApplicationConfig.
     */
    public static ApplicationConfig getInstance() {
        if(instance == null) { instance = new ApplicationConfig(); }
        return instance;
    }

    /**
     * Constructs ApplicationConfig.
     * Loads program configuration from the specified configuration file,
     * and stores the resulting configuration in the new instance.
     * If reading or parsing the configuration fails, a runtime
     * exception will be thrown.
     */
    private ApplicationConfig() {
        try {
            JsonObject config = JsonObject.from(
                    Files.readString(Path.of(LogMap.CONFIG_FILE_PATH))
            );
            this.fromJsonObject(config);

        } catch(IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Application configuration created:\n" + this.toJsonString(true));
    }

    /**
     * The number of simultaneous connections the server will handle
     * @return The value of "threads"
     */
    public int getThreads() { return threads; }

    /**
     * The port the server listens on
     * @return The value of "port"
     */
    public int getPort() { return port; }

    /**
     * The socket timeout for inactive clients
     * @return The value of "timeout"
     */
    public int getTimeout() { return timeout; }

    /**
     * Whether access is restricted to certain IP addresses
     * @return The value of "restricted"
     */
    public boolean isRestricted() { return restricted; }

    /**
     * The list of allowed IP addresses
     * @return The value of "allowed_addresses"
     */
    public Set<String> getAllowedAddresses() { return allowed_addresses; }

    /**
     * The length that component names should be padded to
     * @return The value of "component_pad"
     */
    public int getComponentPad() { return component_pad; }

    /**
     * Whether the program should log to files as well as the console
     * @return The value of "log_to_file"
     */
    public boolean getLogToFile() { return log_to_file; }

    /**
     * The amount of time to be logged in each file
     * @return The value of "file_duration"
     */
    public int getFileDuration() { return file_duration; }

    /**
     * The number of old log files to keep
     * @return The value of "file_history"
     */
    public int getFileHistory() { return file_history; }

    @Override
    public JsonObject toJsonObject() {
        JsonObject server = new JsonObject();
        server.put("threads", new JsonNumber(threads));
        server.put("port", new JsonNumber(port));
        server.put("timeout", new JsonNumber(timeout));
        server.put("restricted", new JsonLiteral(restricted));
        JsonArray addrs = new JsonArray();
        for(String i : allowed_addresses) { addrs.add(new JsonString(i)); }
        server.put("allowed_addresses", addrs);

        JsonObject output = new JsonObject();
        output.put("component_pad", new JsonNumber(component_pad));
        output.put("log_to_file", new JsonLiteral(log_to_file));
        output.put("file_duration", new JsonNumber(file_duration));
        output.put("file_history", new JsonNumber(file_history));

        JsonObject root = new JsonObject();
        root.put("revision", new JsonNumber(revision));
        root.put("server", server);
        root.put("output", output);

        return root;
    }

    @Override
    public void fromJsonObject(JsonObject o) {
        revision = ((JsonNumber) o.get("revision")).toInt();

        final JsonObject config_server = (JsonObject) o.get("server");
        threads = ((JsonNumber) config_server.get("threads")).toInt();
        port = ((JsonNumber) config_server.get("port")).toInt();
        timeout = ((JsonNumber) config_server.get("timeout")).toInt();
        restricted = ((JsonLiteral) config_server.get("restricted")).value();
        allowed_addresses = new TreeSet<>();
        for(JsonEntity i : ((JsonArray) config_server.get("allowed_addresses")).values()) {
            allowed_addresses.add(((JsonString) i).value());
        }

        final JsonObject config_output = (JsonObject) o.get("output");
        component_pad = ((JsonNumber) config_output.get("component_pad")).toInt();
        log_to_file = ((JsonLiteral) config_output.get("log_to_file")).value();
        file_duration = ((JsonNumber) config_output.get("file_duration")).toInt();
        file_history = ((JsonNumber) config_output.get("file_history")).toInt();
    }
}
