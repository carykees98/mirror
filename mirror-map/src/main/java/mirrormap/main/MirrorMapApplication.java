package mirrormap.main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import mirrormap.geoip.GeoIPDatabase;
import mirrormap.geoip.GeoIPUpdater;
import mirrormap.io.WebsocketFrame;
import mirrormap.websocket.WebsocketController;
import mirrormap.websocket.WebsocketServer;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class MirrorMapApplication {

    public static void main(String[] args){
        try (ZContext context = new ZContext()) {
            // Connect to metrics engine
            ZMQ.Socket socket = context.createSocket(SocketType.SUB);
            socket.connect("tcp://mirror-metrics:8081");
            socket.subscribe("");

            // Start websocket server
            WebsocketServer websocketServer = new WebsocketServer(8080);
            websocketServer.start();

            // Start GeoIP database updater
            Thread geoIPUpdater = new Thread(new GeoIPUpdater());
            geoIPUpdater.start();

            // Get GeoIP database handle
            GeoIPDatabase geoIP = GeoIPDatabase.getInstance();

            // Get websocket controller handle
            WebsocketController websocketController = WebsocketController.getInstance();


            while(true) {
                String msg = new String(socket.recv(), StandardCharsets.UTF_8);
                String[] projectIp = msg.split(":", 2);
                if(projectIp.length == 2) {
                    try {
                        double[] latlong = geoIP.getLatLong(projectIp[1]);
                        System.out.println(latlong[0] + " " + latlong[1]);
                        websocketController.broadcast(
                                new WebsocketFrame((byte) 0x1, new byte[]{'t'})
                        );
                    } catch (GeoIp2Exception ignored) {
                        System.err.println("GeoIp2Exception");
                    }
                }
            }




            //DatabaseHandler maxmind = DatabaseHandler.getInstance();

            
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