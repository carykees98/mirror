package org.lavajuno.mirrorlog.server;

import org.lavajuno.mirrorlog.config.ApplicationConfig;
import org.lavajuno.mirrorlog.io.OutputController;
import org.lavajuno.mirrorlog.main.LogMap;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * ServerController accepts incoming connections and assigns
 * them to ServerThreads in the thread pool.
 */
public class ServerController extends Thread {
    /**
     * The socket that the server listens on
     */
    private final ServerSocket socket;

    /**
     * The thread pool that connections are assigned to
     */
    private final ExecutorService threadPool;


    /**
     * This ServerController's OutputController
     */
    final OutputController output_controller;

    /**
     * Instantiates a ServerController.
     * @throws IOException if the socket cannot be created
     */
    public ServerController() throws IOException, ParseException {
        output_controller = new OutputController();
        threadPool = Executors.newFixedThreadPool(ApplicationConfig.getInstance().getThreads());
        socket = new ServerSocket(ApplicationConfig.getInstance().getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(this::interrupt));
    }

    @Override
    public void run() {
        output_controller.start();
        output_controller.submitEvent(
                "Log Server",
                0,
                "Startup complete."
        );
        while(true) {
            try {
                threadPool.submit(new ServerThread(socket.accept(), output_controller));
            } catch(IOException e) {
                if (socket.isClosed()) { return; }
                System.err.println("Failed to accept a connection. (IOException)");
            } catch(RejectedExecutionException e) {
                System.err.println("Failed to accept a connection. (Thread pool is full)");
            }
        }
    }

    @Override
    public void interrupt() {
        System.out.println("Closing server socket...");
        try {
            socket.close();
        } catch(IOException e) {
            System.err.println("Failed to close server socket. (IOException)");
        }

        System.out.println("Shutting down output controller...");
        output_controller.interrupt();

        try {
            output_controller.join(LogMap.IO_SHUTDOWN_TIMEOUT);
            if(output_controller.isAlive()) {
                System.out.println("Still waiting on output controller to shut down.");
            }
        } catch(InterruptedException e) {
            System.err.println("Interrupted while shutting down output controller. Skipping timeout.");
        }
    }
}
