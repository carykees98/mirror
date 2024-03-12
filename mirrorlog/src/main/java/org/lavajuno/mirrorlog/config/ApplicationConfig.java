package org.lavajuno.mirrorlog.config;

import org.lavajuno.lucidjson.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Vector;

/**
 * ApplicationConfig loads and stores the program configuration, and
 * provides functionality for retrieving configuration values.
 */
@SuppressWarnings("unused")
public class ApplicationConfig {
    private final int threads;
    private final int port;
    private final int timeout;
    private final int revision;
    private final boolean restricted;
    private final Vector<InetAddress> allowed_addresses;
    private final int component_pad;
    private final boolean log_to_file;
    private final int file_duration;
    private final int file_history;

    /**
     * Instantiates an ApplicationConfig.
     * Loads program configuration from the specified configuration file,
     * and stores the resulting configuration in the instance.
     * @param config_file_path Path to the configuration file to load
     * @throws IOException If loading the configuration fails.
     */
    public ApplicationConfig(String config_file_path) throws IOException, ParseException {
        /* Parse configuration file */
        final JsonObject config_root = JsonObject.fromFile(config_file_path);

        /* Get configuration revision */
        final JsonNumber config_revision = (JsonNumber) config_root.get("revision");

        /* Get server configuration */
        final JsonObject config_server = (JsonObject) config_root.get("server");
        final JsonNumber config_threads = (JsonNumber) config_server.get("threads");
        final JsonNumber config_port = (JsonNumber) config_server.get("port");
        final JsonNumber config_timeout = (JsonNumber) config_server.get("timeout");
        final JsonLiteral config_restricted = (JsonLiteral) config_server.get("restricted");
        final JsonArray config_addresses = (JsonArray) config_server.get("allowed_addresses");

        /* Get log file configuration */
        final JsonObject config_output = (JsonObject) config_root.get("output");
        final JsonNumber config_component_pad = (JsonNumber) config_output.get("component_pad");
        final JsonLiteral config_log_to_file = (JsonLiteral) config_output.get("log_to_file");
        final JsonNumber config_file_duration = (JsonNumber) config_output.get("file_duration");
        final JsonNumber config_file_history = (JsonNumber) config_output.get("file_history");

        /* Read in and set configuration values */
        try {
            this.revision = config_revision.getInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"version\".");
            throw new IOException("Illegal value for key \"version\".");
        }

        try {
            this.threads = config_threads.getInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"threads\".");
            throw new IOException("Illegal value for key \"threads\".");
        }

        try {
            this.port = config_port.getInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"port\".");
            throw new IOException("Illegal value for key \"port\".");
        }

        try {
            this.timeout = config_timeout.getInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"timeout\".");
            throw new IOException("Illegal value for key \"timeout\".");
        }

        this.restricted = config_restricted.getValue();

        allowed_addresses = new Vector<>();
        try {
            for(JsonEntity i : config_addresses.getValues()) {
                JsonString j = (JsonString) i;
                allowed_addresses.add(InetAddress.getByName(j.getValue()) );
            }
        } catch(UnknownHostException e) {
            System.err.println("Illegal value for key \"allowed_addresses\".");
            throw new IOException("Illegal value for key \"allowed_addresses\".");
        }

        try {
            this.component_pad = config_component_pad.getInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"component_pad\".");
            throw new IOException("Illegal value for key \"component_pad\".");
        }

        this.log_to_file = config_log_to_file.getValue();

        try {
            this.file_duration = config_file_duration.getInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"file_duration\".");
            throw new IOException("Illegal value for key \"file_duration\".");
        }

        try {
            this.file_history = config_file_history.getInt();
        } catch(NumberFormatException e) {
            System.err.println("Illegal value for key \"file_history\".");
            throw new IOException("Illegal value for key \"file_history\".");
        }

        /* Finished loading configuration */
        System.out.println("Application configuration created:\n" + this);
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
    public Vector<InetAddress> getAllowedAddresses() { return allowed_addresses; }

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
    public String toString() {
        return String.format(
            """
            {
                Revision: %s
                Threads: %s
                Port: %s
                Timeout: %s
                Restricted: %s
                Allowed addresses: %s
                Component padding: %s
                Log to file: %s
                File duration: %s
                File history: %s
            }""",
            revision,
            threads,
            port,
            timeout,
            restricted,
            allowed_addresses,
            component_pad,
            log_to_file,
            file_duration,
            file_history
        );
    }
}
