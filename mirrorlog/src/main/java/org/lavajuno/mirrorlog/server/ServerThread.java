package org.lavajuno.mirrorlog.server;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

import org.lavajuno.mirrorlog.config.ApplicationConfig;
import org.lavajuno.mirrorlog.io.OutputController;

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
            if(application_config.isRestricted() &&
                    !application_config.getAllowedAddresses().contains(client_address)) {
                socket.close();
                return;
            }
            outputController.submitEvent(
                    "Log Server",
                    0,
                    "Client at " + client_address + " connected."
            );
            socket.setSoTimeout(application_config.getTimeout());
            Scanner inFromClient = new Scanner(socket.getInputStream());
            PrintWriter outToClient = new PrintWriter(socket.getOutputStream());
            // Loop while the stream is open
            while(inFromClient.hasNext()) {
                String line = inFromClient.nextLine();
                if(line.matches("^@[0-9A-Za-z_ -]{1,128}@[0-3].*$")) {
                    String[] fragments = line.split("@", 3);
                    // Submit log event and respond
                    outputController.submitEvent(
                            fragments[1],
                            Integer.parseInt(fragments[2].substring(0, 1)),
                            fragments[2].substring(1)
                    );
                    outToClient.println(line);
                    outToClient.flush();
                } else {
                    outToClient.println("BAD SYNTAX");
                    outToClient.flush();
                }
            }
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
}
