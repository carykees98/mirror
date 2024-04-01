package mirrormap;

import java.io.IOException;

import mirrormap.server.WebsocketServer;

public class MirrorMapApplication {

    public static void main(String[] args){
        try{
            WebsocketServer websocketServer = new WebsocketServer(8086);
            websocketServer.start();
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
        
        System.out.println("main() exited.");
    }
}