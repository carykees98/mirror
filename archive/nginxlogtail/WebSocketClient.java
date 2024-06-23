package mirrormap.nginxlogtail;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CompletableFuture;

public class WebSocketClient {
    private WebSocket webSocket;

    public WebSocketClient(String uri) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        // Implement the WebSocket.Listener interface
        Listener listener = new Listener() {
            @Override
            public void onOpen(WebSocket webSocket) {
                System.out.println("WebSocket connection opened");
            }

            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                System.out.println("Received text: " + data);
                // Return a completed CompletionStage
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                System.out.println("WebSocket connection closed");
                // Return a completed CompletionStage
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public void onError(WebSocket webSocket, Throwable error) {
                System.out.println("WebSocket error: " + error.getMessage());
            }
        };

        // Build the WebSocket with the URI and listener
        this.webSocket = httpClient.newWebSocketBuilder()
                .buildAsync(URI.create(uri), listener)
                .join(); // Block until the WebSocket is open
    }

    public void sendLogEntry(LogEntry entry) {
        if (webSocket != null && !webSocket.isOutputClosed()) {
            webSocket.sendText(entry.toString(), true);
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.abort();
        }
    }
}
