package mirrormap.nginxlogtail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {
    private static final Pattern LOG_PATTERN = Pattern.compile("^(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}) .*?\"GET /([^\"]+)\"");

    public LogEntry parse(String logLine) {
        Matcher matcher = LOG_PATTERN.matcher(logLine);
        if (matcher.find()) {
            String ipAddress = matcher.group(1);
            String project = matcher.group(2);
            return new LogEntry(ipAddress, project);
        } else {
            return null;
        }
    }
}
