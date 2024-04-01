package mirrormap;

import java.io.IOException;

import mirrormap.server.ServerController;

public class MirrorMapApplication {

    public static void main(String[] args){
        try{
            ServerController serverController = new ServerController(8086);
            serverController.start();
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
        
        System.out.println("end of main function");
    }
}