package org.lavajuno.mirrorlog.io;

import org.lavajuno.mirrorlog.config.ApplicationConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;

/**
 * LogFile handles creating, writing to, and deleting log files for OutputController.
 */
public class LogFile {
    private static final String LOGS_PATH = "logs/";
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHH");
    private final long DATE_EXPIRY;
    private final FileWriter log_file_writer;
    private final PrintWriter log_print_writer;

    /**
     * Constructs a LogFile.
     * Creates new files and cleans up old ones if needed.
     * @throws IOException Passes along IOExceptions from file accessors
     */
    public LogFile() throws IOException {
        final String DATE_TAG = FILE_DATE_FORMAT.format(new Date());
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.HOUR, ApplicationConfig.getInstance().getFileDuration());
        DATE_EXPIRY = Long.parseLong(FILE_DATE_FORMAT.format(calendar.getTime()));
        cleanupLogs(LOGS_PATH, ApplicationConfig.getInstance().getFileHistory());

        Files.createDirectories(Paths.get(LOGS_PATH));
        final String FILE_PATH = LOGS_PATH + DATE_TAG + ".log";
        log_file_writer = new FileWriter(FILE_PATH, StandardCharsets.UTF_8, true);
        log_print_writer = new PrintWriter(log_file_writer, true);
    }

    /**
     * Prints a log event to the file.
     * @param event Log event to print
     */
    public void print(LogEvent event) {
        log_print_writer.println(event);
    }

    /**
     * Flushes output buffers and closes the file.
     */
    public void close() {
        try {
            log_print_writer.flush();
            log_file_writer.flush();
            log_print_writer.close();
            log_file_writer.close();
        } catch(IOException e) {
            System.err.println("Failed to close log file writer.");
        }
    }

    /**
     * Checks if this LogFile is expired.
     * @return True if this LogFile is expired.
     */
    public boolean isExpired() {
        return DATE_EXPIRY < Long.parseLong(FILE_DATE_FORMAT.format(new Date()));
    }

    /**
     * Cleans up old logs
     * @param path Path to log directory
     * @param max_logs Amount of log files to keep
     */
    public void cleanupLogs(String path, int max_logs) {
        File[] path_contents = new File(path).listFiles();
        if(path_contents == null || path_contents.length == 0) { return; }
        Vector<String> path_datetags = new Vector<>();
        String filename;
        for(File i : path_contents) {
            if(i.isFile()) {
                filename = i.getName().split("\\.", 2)[0];
                try {
                    Integer.parseInt(filename);
                } catch(NumberFormatException e) {
                    path_datetags.add(filename);
                }
            }
        }
        int n_extra_logs = path_datetags.size() - max_logs;
        if(n_extra_logs > 0) {
            Collections.sort(path_datetags);
            for(int i = 0; i < n_extra_logs; i++) {
                try {
                    Files.delete(Paths.get(LOGS_PATH + path_datetags.get(i) + ".log"));
                } catch(IOException e) {
                    System.err.println("Failed to delete old log \"" + path_datetags.get(i) + ".log\".");
                }
            }
        }
    }
}
