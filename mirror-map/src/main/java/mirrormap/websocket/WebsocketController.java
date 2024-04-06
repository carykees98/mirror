package mirrormap.websocket;

import mirrormap.io.WebsocketFrame;

import java.util.Vector;

/**
 * WebsocketController stores a list of active connections and provides
 * functionality to broadcast frames to every connected client.
 * WebsocketServerThreads will register themselves with the WebsocketController
 * upon connection, and deregister themselves when they disconnect or if an exception
 * is thrown.
 */
public class WebsocketController {
    private final Vector<WebsocketServerThread> connections;
    private static WebsocketController instance = null;

    /**
     * Private constructor for WebsocketController.
     */
    private WebsocketController() {
        connections = new Vector<>();
    }

    /**
     * Gets the instance of WebsocketController, creating it if it does not already exist.
     * @return WebsocketController instance
     */
    public static synchronized WebsocketController getInstance() {
        if(instance == null) { instance = new WebsocketController(); }
        return instance;
    }

    /**
     * Broadcasts a WebsocketFrame to every currently connected client.
     * @param f WebsocketFrame to broadcast
     */
    public synchronized void broadcast(WebsocketFrame f) {
        for(WebsocketServerThread i : connections) {
            i.sendFrame(f);
        }
    }

    /**
     * Registers a WebsocketServerThread with this WebsocketController.
     * It is the responsibility of each WebsocketServerThread to deregister itself
     * if an error occurs or the client closes the connection.
     * @param session WebsocketServerThread instance to register (usually "this")
     */
    public void register(WebsocketServerThread session) { connections.add(session); }

    /**
     * Deregisters a WebsocketServerThread from this WebsocketController.
     * @param session WebsocketServerThread instance to deregister (usually "this")
     */
    public void deregister(WebsocketServerThread session) { connections.remove(session); }
}
