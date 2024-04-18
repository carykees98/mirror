package org.lavajuno.mirrorlog.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import org.lavajuno.mirrorlog.config.ApplicationConfig;
import org.lavajuno.mirrorlog.io.OutputController;
import org.lavajuno.mirrorlog.main.LogMap;

/**
 * ServerThread serves a single client and queues events
 * for the OutputController to process.
 */
public class ServerThread extends Thread {
    private final Socket socket;
    private final OutputController outputController;
    private final ApplicationConfig application_config;
    private final String client_address;

    /**
     * Instantiates a ServerThread.
     * @param socket Socket to communicate with client over
     * @param outputController OutputController to queue events in
     */
    public ServerThread(Socket socket, OutputController outputController) {
        this.socket = socket;
        client_address = socket.getInetAddress().toString().split("/", 2)[1];
        this.outputController = outputController;
        this.application_config = ApplicationConfig.getInstance();
        Runtime.getRuntime().addShutdownHook(new Thread(this::interrupt));
    }

    @Override
    public void run() {
        try {
            // Ignore unauthorized clients
            if(application_config.isRestricted() &&
                    !application_config.getAllowedAddresses().contains(client_address)) {
                socket.close();
                return;
            }
            // Set up socket, input stream, and buffers
            socket.setSoTimeout(application_config.getTimeout());
            InputStream inFromClient = socket.getInputStream();
            byte[] in_buf = new byte[LogMap.EVENT_BUFFER_SIZE];
            int in_buf_idx = 0;

            outputController.submitEvent(
                    "Log Server",
                    0,
                    "Client at " + client_address + " connected."
            );

            // Read from stream (break on event buffer overflow)
            while(in_buf_idx < LogMap.EVENT_BUFFER_SIZE) {
                int b = inFromClient.read();
                if(b == -1) { break; } // End of stream, break
                if(b == '\n') { // Line break, queue event
                    queueEvent(new String(in_buf, 0, in_buf_idx, StandardCharsets.UTF_8));
                    in_buf_idx = 0;
                } else if(b != '\r') { // Read into event buffer
                    in_buf[in_buf_idx] = (byte) b;
                    in_buf_idx++;
                }
            }

            // Clean up
            inFromClient.close();
            socket.close();
            outputController.submitEvent(
                    "Log Server",
                    0,
                    "Client at " + client_address + " disconnected."
            );
        } catch(SocketException e) {
            outputController.submitEvent(
                    "Log Server",
                    2,
                    "Client at " + client_address + " disconnected. (SocketException)"
            );
        } catch(IOException e) {
            outputController.submitEvent(
                    "Log Server",
                    2,
                    "Client at " + client_address + " disconnected. (IOException)"
            );
        }
    }

    @Override
    public void interrupt() {
        try {
            System.out.println("Connection to " + client_address + " terminated. (Shutdown)");
            this.socket.close();
        } catch(IOException e) {
            System.err.println("Failed to close connection to " + client_address);
        }
    }

    /**
     * Queues the given event to be logged. If it is malformed,
     * it will instead queue a warning that this is the case.
     * @param event Event to queue (represented as a String)
     */
    private void queueEvent(String event) {
        if(event.matches("^@[0-9A-Za-z_ -]{1,128}@[0-3].*$")) {
            String[] fragments = event.split("@", 3);
            outputController.submitEvent( // Queue event
                    fragments[1],
                    Integer.parseInt(fragments[2].substring(0, 1)),
                    fragments[2].substring(1)
            );
        } else {
            outputController.submitEvent( // Report bad event
                    "Log Server",
                    1,
                    "Received bad event from " + client_address
            );
        }
    }
}
