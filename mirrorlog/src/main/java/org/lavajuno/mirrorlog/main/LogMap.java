package org.lavajuno.mirrorlog.main;

import java.text.SimpleDateFormat;

/**
 * The purpose of LogMap is to eliminate the presence of magic numbers in the program.
 * Some of these parameters may eventually be moved to the configuration file.
 * Others, like the path to the config file and the size of the input buffer, should stay here.
 */
public class LogMap {
    /**
     * The path to the program's configuration file
     */
    public static final String CONFIG_FILE_PATH = "config/mirrorlog.conf.json";

    /**
     * How long ServerController should wait for OutputController to shut down (ms)
     */
    public static final int IO_SHUTDOWN_TIMEOUT = 5000;

    /**
     * The date and time format of log events.
     */
    public static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Color and header of messages with severity 0
     */
    public static final String SEVERITY_INFO_PRETTY = " \u001B[32m[ INFO ]\u001B[0m  ";
    public static final String SEVERITY_INFO = " [ INFO ]  ";

    /**
     * Color and header of messages with severity 1
     */
    public static final String SEVERITY_WARN_PRETTY = " \u001B[33m[ WARN ]\u001B[0m  ";
    public static final String SEVERITY_WARN = " [ WARN ]  ";

    /**
     * Color and header of messages with severity 2
     */
    public static final String SEVERITY_ERROR_PRETTY = " \u001B[31m[ ERROR ]\u001B[0m ";
    public static final String SEVERITY_ERROR = " [ ERROR ] ";

    /**
     * Color and header of messages with severity 3
     */
    public static final String SEVERITY_FATAL_PRETTY = " \u001B[31m[ FATAL ]\u001B[0m ";
    public static final String SEVERITY_FATAL = " [ FATAL ] ";
}
