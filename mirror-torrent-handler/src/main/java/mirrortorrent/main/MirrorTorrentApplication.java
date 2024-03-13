package mirrortorrent.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Thread;
import java.text.ParseException;

import org.lavajuno.lucidjson.JsonArray;
import org.lavajuno.lucidjson.JsonObject;
import org.lavajuno.lucidjson.JsonString;

import mirrortorrent.torrents.*;

public class MirrorTorrentApplication {    
    public static void main(String[] args){

        try{
            //read torrent folder path from env.json file
            JsonObject env = JsonObject.fromFile("configs/env.json");
            String torrentFolder = ((JsonString) env.get("torrentFolder")).getValue();
            String downloadFolder = ((JsonString) env.get("downloadFolder")).getValue();

            //if torrent folder doesnt exist create it
            File dir = new File(torrentFolder);
            if(!dir.exists()){
                dir.mkdir();
            }

            File dir2 = new File(downloadFolder);
            if(!dir2.exists()){
                dir2.mkdir();
            }

            //load mirrors.json
            JsonObject config = JsonObject.fromFile("configs/mirrors.json");

            Thread torrentScrapeThread = new Thread( new ScrapeTorrents((JsonArray) config.get("torrents"), torrentFolder));
            torrentScrapeThread.start();

            Thread syncTorrentsThread = new Thread( new SyncTorrents((JsonObject) config.get("mirrors"), torrentFolder, downloadFolder));
            syncTorrentsThread.start();
            
        }
        catch(FileNotFoundException | ParseException e){
            e.printStackTrace();
        }
    }
}
