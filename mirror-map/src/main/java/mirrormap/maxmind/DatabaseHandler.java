package mirrormap.maxmind;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

public class DatabaseHandler {

    private static final String DATABASE_DESTINATION_DIR = "GeoLite2City";

    //used to read data from the maxmind database
    private static DatabaseReader reader = null;

    //used to make this class a singleton class
    private static DatabaseHandler dbh_instance = null;

    //private default constructor for singleton
    private DatabaseHandler(){}

    //get instance function for accessing the singleton class
    public static synchronized DatabaseHandler getInstance(){
        if(dbh_instance == null){
            dbh_instance = new DatabaseHandler();
        }
        return dbh_instance;
    }

    //initialize the maxmind database handler based on its file location
    public void ConfigureHandler(){
        try{
            File geoLite2Directory = new File(DATABASE_DESTINATION_DIR + "/" + new File(DATABASE_DESTINATION_DIR).list()[0] + "/GeoLite2-City.mmdb");
            reader = new DatabaseReader.Builder(geoLite2Directory).build();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    //used to get the latitude and longitude based on the ip using the maxmind database reader
    public double[] getLatLong(String ipAddress) throws IOException, GeoIp2Exception{
        if(reader == null){
            throw new GeoIp2Exception("Database Reader Not Configured.");
        }
        InetAddress ip = InetAddress.getByName(ipAddress);
        CityResponse response = reader.city(ip);
 
        double[] longlat = new double[2];
        longlat[0] = response.getLocation().getLatitude();
        longlat[1] = response.getLocation().getLongitude();
        return longlat;
    }

}
