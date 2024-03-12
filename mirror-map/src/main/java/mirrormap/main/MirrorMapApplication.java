package mirrormap.main;

import java.io.IOException;
import java.lang.Thread;

import com.maxmind.geoip2.exception.GeoIp2Exception;

import mirrormap.maxmind.DatabaseHandler;
import mirrormap.maxmind.DatabaseUpdater;

public class MirrorMapApplication {

    public static void main(String[] args){
        try{
            DatabaseHandler maxmind = DatabaseHandler.getInstance();
            Thread maxmindUpdater = new Thread(new DatabaseUpdater());
            maxmindUpdater.start();
            
            Thread.sleep(10000);
            double [] latlong = maxmind.getLatLong("128.153.197.71");
            System.out.println(latlong[0] + " " + latlong[1]);
        }
        catch(IOException | GeoIp2Exception | InterruptedException e){
            e.printStackTrace();
            return;
        }
        
        System.out.println("end of main function");
    }
}