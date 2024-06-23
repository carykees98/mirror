package mirrormap.nginxlogtail;

import java.nio.file.*;
import java.io.IOException;
import java.util.List;

public class LogMonitor {
    private Path logPath;
    private WatchService watchService;
    private LogParser logParser;
    private WebSocketClient webSocketClient;

    public LogMonitor(String logPathStr, LogParser logParser, WebSocketClient webSocketClient) throws IOException {
        this.webSocketClient = webSocketClient;
        this.logPath = Paths.get(logPathStr);
        this.watchService = FileSystems.getDefault().newWatchService();
        this.logParser = logParser;
        this.logPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public void monitor() throws IOException, InterruptedException {
        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    processModifiedLogFile((Path) event.context());
                }
            }
            key.reset();
        }
    }

    private void processModifiedLogFile(Path modifiedPath) throws IOException {
        List<String> lines = Files.readAllLines(modifiedPath);
        for (String line : lines) {
            LogEntry entry = logParser.parse(line);
            if (entry != null) {
                sendToWebSocket(entry);
            }
        }
    }

    private void sendToWebSocket(LogEntry entry) {
        webSocketClient.sendLogEntry(entry);
    }
}