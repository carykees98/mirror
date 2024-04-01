package mirrormap.server;
import mirrormap.io.HttpRequest;
import mirrormap.io.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Base64;

public class ServerThread extends Thread {
    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;

    public ServerThread(Socket socket) throws IOException {
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

        } catch(ParseException e) {
            e.printStackTrace();
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        try {
            socket.close();
        } catch(IOException e) {
            System.err.println("L");
        }
    }

    private String getWsAccept(String ws_key) throws NoSuchAlgorithmException {
        byte[] linked = (ws_key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(
                MessageDigest.getInstance("SHA-1").digest(linked)
        );
    }
}
