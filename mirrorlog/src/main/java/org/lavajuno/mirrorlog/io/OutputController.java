package org.lavajuno.mirrorlog.io;

import org.lavajuno.mirrorlog.config.ApplicationConfig;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * OutputController queues log entries from multiple ServerThreads
 * and handles printing them to the console as well as writing them to files.
 */
public class OutputController extends Thread {
    private final BlockingQueue<LogEvent> output_queue;
    private final ApplicationConfig application_config;
    private LogFile logFile;
    private final boolean LOG_TO_FILE;

    /**
     * Instantiates an OutputController.
     * @param application_config ApplicationConfig to use
     */
    public OutputController(ApplicationConfig application_config) throws IOException {
        this.application_config = application_config;
        this.output_queue = new LinkedBlockingQueue<>();
        this.LOG_TO_FILE = application_config.getLogToFile();
        if(LOG_TO_FILE) { logFile = new LogFile(application_config); }
    }

    /**
     * Submits an event to be logged
     * @param component_name Component name of the log event
     * @param severity Severity of the log event
     * @param message Message to be logged
     */
    public void submitEvent(String component_name, int severity, String message) {
        output_queue.add(new LogEvent(component_name, severity, message, application_config));
    }

    /**
     * OutputController's thread.
     */
    @Override
    public void run() {
        LogEvent event = null; /* needs to be initialized for exception handler */
        try {
            while(true) {
                event = output_queue.take();
                write(event);

            }
        } catch(InterruptedException e) {
            System.out.println("Flushing output queue...");
            while((event = output_queue.poll()) != null) { write(event); }
            System.out.println("Writing to log file...");
            logFile.close();
        }
    }

    /**
     * Writes an event to the log.
     * @param event LogEvent to write to the log.
     */
    private void write(LogEvent event) {
        if(LOG_TO_FILE) {
            if(logFile.isExpired()) {
                logFile.close();
                try {
                    logFile = new LogFile(application_config);
                } catch(IOException e) {
                    System.err.println("Failed to create new log file!");
                }
            }
            logFile.print(event);
        }
        System.out.println(event.toPrettyString());
    }
}
