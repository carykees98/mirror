package mirrormap.main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import mirrormap.io.WebsocketFrame;
import mirrormap.server.WebsocketController;
import mirrormap.server.WebsocketServer;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZSocket;

public class MirrorMapApplication {

    public static void main(String[] args){
        try (ZContext context = new ZContext()) {
            ZMQ.Socket socket = context.createSocket(SocketType.PUB);
            socket.connect("tcp://mirror-metrics:8081");
            socket.send(new byte[]{0x01});

            WebsocketServer websocketServer = new WebsocketServer(8080);
            websocketServer.start();

            WebsocketController websocketController = WebsocketController.getInstance();

            while(true) {
                websocketController.broadcast(
                        new WebsocketFrame((byte) 0x1, socket.recv())
                );
            }




            //DatabaseHandler maxmind = DatabaseHandler.getInstance();
            //Thread maxmindUpdater = new Thread(new DatabaseUpdater());
            //maxmindUpdater.start();
            
            //Thread.sleep(10000);
            //double [] latlong = maxmind.getLatLong("128.153.197.71");
            //System.out.println(latlong[0] + " " + latlong[1]);
        }
        catch(IOException e) {
            e.printStackTrace();
            return;
        }
    }
}