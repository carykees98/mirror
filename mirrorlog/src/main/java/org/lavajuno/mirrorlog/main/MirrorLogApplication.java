package org.lavajuno.mirrorlog.main;

import java.io.IOException;
import java.text.ParseException;

import org.lavajuno.mirrorlog.server.ServerController;

/**
 * MirrorLogApplication handles startup of the server.
 */
public class MirrorLogApplication {

    /**
     * Starts MirrorLog.
     * @param args Unused
     */
    public static void main(String[] args) throws IOException, ParseException {
        ServerController server_controller;
        System.out.println("Starting MirrorLog server...");
        server_controller = new ServerController();
        server_controller.start();
    }
}
