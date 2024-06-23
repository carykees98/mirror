package mirrormap.nginxlogtail;

public class LogEntry {
    private String ipAddress;
    private String project;

    public LogEntry(String ipAddress, String project) {
        this.ipAddress = ipAddress;
        this.project = project;
    }

    public String getIP() {
        return ipAddress;
    }

    public String getProject() {
        return project;
    }
}
