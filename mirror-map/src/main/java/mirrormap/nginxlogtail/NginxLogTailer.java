
import java.nio.file.*;

import javax.websocket.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


//create a log entry class that stores ip address and project needed to send to websocket.
private class LogEntry {

	private String ipAddress;
	private String project;

	public LogEntry(String ipAddress, String project) {
    	this.ipAddress = ipAddress;
    	this.project = project;
	}

	//getters
	public getIP(){return ipAddress;}
	public getProject(){return project;}

	//setters? do I need setters?
}


//take a log line and retrun a log entry

//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
public class LogParser {

	//need actual log pattern?
	private static final Pattern LOG_PATTERN = Pattern.compile("^(\\d{1,3}\\.\\d {1,3}\\.\\d{1,3}\\.\\d{1,3}) .*?\"GET /([^\"]+)\"");

	public LogEntry parse(String logLine) {
    	Matcher matcher = LOG_PATTERN.matcher(logLine);
    	if (matcher.find()) {
			//figure out what matcher.group is
        	String ipAddress = matcher.group(1);
        	String project = matcher.group(2);
        	return new LogEntry(ipAddress, project);
    	} 
		
		else {return null;}
	}
}

//use watchservice api to monitor log file for changes

//import java.nio.file.*;
public class LogMonitor {

	private Path logPath;
	private WatchService watchService;
	private LogParser logParser;
	private WebSocketClient webSocketClient;


	public LogMonitor(String logPathStr, LogParser logParser, WebSocketClient webSocketClient) throws IOException {

		this.webSocketClient = webSocketClient
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
    	// Read the last few lines of the log file and parse them
    	List<String> lines = Files.readAllLines(modifiedPath);
    	for (String line : lines) {
        	LogEntry entry = logParser.parse(line);
        	if (entry != null) {
            	// Send the entry to the WebSocket
            	sendToWebSocket(entry);
        	}
    	}
	}

	private void sendToWebSocket(LogEntry entry) {webSocketClient.sendLogEntry(entry);}
	
}


//implement the sendtowebsocket method
//import javax.websocket.*;
@ClientEndpoint
public class WebSocketClient {

	private Session session;
	@OnOpen
	public void onOpen(Session session) {
    	this.session = session;
	}
	public void sendLogEntry(LogEntry entry) {
    	if (session != null && session.isOpen()) {
        	session.getBasicRemote().sendText(entry.toString());
    	}
	}
}



//main function, switch to run() at some point
//add nginx path
public static void main(String[] args) throws Exception {
	LogParser logParser = new LogParser();
	WebSocketClient webSocketClient = new WebSocketClient();
	LogMonitor logMonitor = new LogMonitor("/path/to/nginx/access.log", log
Parser, webSocketClient);
	logMonitor.monitor();
}
