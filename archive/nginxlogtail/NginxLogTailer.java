package mirrormap.nginxlogtail;

import java.io.IOException;

public class NginxLogTailer {
    public static void main(String[] args) {
		LogParser logParser = new LogParser();
		String webSocketUri = "ws://your-websocket-server"; // Replace with your WebSocket server URI
		WebSocketClient webSocketClient = new WebSocketClient(webSocketUri);
		try {
			LogMonitor logMonitor = new LogMonitor("/path/to/nginx/access.log", logParser, webSocketClient);
			
			logMonitor.monitor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
