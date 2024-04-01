package mirrormap.server;

import mirrormap.io.WebsocketFrame;

import java.util.Vector;

public class WebsocketController {
    private final Vector<WebsocketServerThread> connections;
    private static WebsocketController instance = null;

    private WebsocketController() {
        connections = new Vector<>();
    }

    public static synchronized WebsocketController getInstance() {
        if(instance == null) { instance = new WebsocketController(); }
        return instance;
    }

    public synchronized void broadcast(WebsocketFrame f) {
        for(WebsocketServerThread i : connections) {
            i.sendFrame(f);
        }
    }

    public void register(WebsocketServerThread session) { connections.add(session); }

    public void deregister(WebsocketServerThread session) { connections.remove(session); }
}
