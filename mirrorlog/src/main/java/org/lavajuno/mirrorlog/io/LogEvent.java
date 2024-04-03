package org.lavajuno.mirrorlog.io;

import org.lavajuno.mirrorlog.config.ApplicationConfig;
import org.lavajuno.mirrorlog.main.LogMap;

import java.util.Date;

/**
 * LogEvent stores a single log event and provides functionality to
 * convert it to a string to be printed to the console or a file.
 */
public class LogEvent {
    /**
     * The length to pad component names to
     */
    private final int COMPONENT_PAD;

    /**
     * The component name for this LogEvent
     */
    private final String component_name;

    /**
     * The message for this LogEvent
     */
    private final String message;

    /**
     * The severity for this LogEvent
     */
    private final int severity;

    /**
     * Instantiates a LogEvent.
     * @param component_name The component name to be logged
     * @param severity The severity of the event
     * @param message The message to be logged
     */
    public LogEvent(String component_name, int severity, String message) {
        this.component_name = component_name;
        this.severity = severity;
        this.message = message;
        this.COMPONENT_PAD = ApplicationConfig.getInstance().getComponentPad();
    }

    /**
     * Like toString, but with colored status indicators.
     * @return This LogEvent as a string.
     */
    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LogMap.LOG_DATE_FORMAT.format(new Date()));
        switch(severity) {
            case 1:
                sb.append(LogMap.SEVERITY_WARN_PRETTY);
                break;
            case 2:
                sb.append(LogMap.SEVERITY_ERROR_PRETTY);
                break;
            case 3:
                sb.append(LogMap.SEVERITY_FATAL_PRETTY);
                break;
            default:
                sb.append(LogMap.SEVERITY_INFO_PRETTY);
                break;
        }
        sb.append(component_name);
        sb.append(" ".repeat(Math.max(0, COMPONENT_PAD - component_name.length())));
        sb.append(" : ");
        sb.append(message);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LogMap.LOG_DATE_FORMAT.format(new Date()));
        switch(severity) {
            case 1:
                sb.append(LogMap.SEVERITY_WARN);
                break;
            case 2:
                sb.append(LogMap.SEVERITY_ERROR);
                break;
            case 3:
                sb.append(LogMap.SEVERITY_FATAL);
                break;
            default:
                sb.append(LogMap.SEVERITY_INFO);
                break;
        }
        sb.append(component_name);
        sb.append(" ".repeat(Math.max(0, COMPONENT_PAD - component_name.length())));
        sb.append(" : ");
        sb.append(message);
        return sb.toString();
    }
}
