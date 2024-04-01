package mirrormap.server;
import mirrormap.io.HttpRequest;
import mirrormap.io.HttpResponse;
import mirrormap.io.WebsocketFrame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Base64;

public class WebsocketServerThread extends Thread {
    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;

    public WebsocketServerThread(Socket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    @Override
    public void run() {
        try {
            HttpRequest req = new HttpRequest(in);
            String ws_key = req.getHeader("Sec-WebSocket-Key");
            System.out.println(ws_key);
            HttpResponse res = new HttpResponse("101 Switching Protocols");
            res.setHeader("Connection", "Upgrade");
            res.setHeader("Upgrade", "websocket");
            res.setHeader("Sec-WebSocket-Accept", getWsAccept(ws_key));
            out.write(res.toBytes());

            WebsocketController.getInstance().register(this);

        } catch(ParseException e) {
            WebsocketController.getInstance().deregister(this);
            System.err.println("WebsocketServerThread encountered a ParseException.");
            e.printStackTrace();
        } catch(NoSuchAlgorithmException e) {
            WebsocketController.getInstance().deregister(this);
            System.err.println("WebsocketServerThread encountered a NoSuchAlgorithmException.");
            e.printStackTrace();
        } catch(IOException e) {
            WebsocketController.getInstance().deregister(this);
            System.err.println("WebsocketServerThread encountered an IOException.");
            e.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        WebsocketController.getInstance().deregister(this);
        try {
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendFrame(WebsocketFrame f) {
        try {
            out.write(f.toBytes());
        } catch(IOException e) {
            System.err.println("IOException while sending frame.");
            e.printStackTrace();
        }
    }

    private String getWsAccept(String ws_key) throws NoSuchAlgorithmException {
        byte[] linked = (ws_key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(
                MessageDigest.getInstance("SHA-1").digest(linked)
        );
    }
}
