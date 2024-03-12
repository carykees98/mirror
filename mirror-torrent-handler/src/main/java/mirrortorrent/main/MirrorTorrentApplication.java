package mirrortorrent.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Thread;
import java.text.ParseException;

import org.lavajuno.lucidjson.JsonArray;
import org.lavajuno.lucidjson.JsonObject;

import io.github.cdimascio.dotenv.Dotenv;

import mirrortorrent.torrents.*;

public class MirrorTorrentApplication {    
    public static void main(String[] args){
        //read torrent folder path from .env file
        Dotenv dotenv = Dotenv.load();
        String torrentFolder = dotenv.get("TorrentFolder");
        String downloadFolder = dotenv.get("DownloadFolder");

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
        try{
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
