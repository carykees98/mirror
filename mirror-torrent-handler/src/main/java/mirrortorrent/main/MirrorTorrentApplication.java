package mirrortorrent.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Thread;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.lavajuno.lucidjson.JsonArray;
import org.lavajuno.lucidjson.JsonObject;
import org.lavajuno.lucidjson.JsonString;

import mirrortorrent.torrents.*;
import mirrortorrent.io.*;

public class MirrorTorrentApplication {    
    public static void main(String[] args){

        try{
            //read torrent folder path from env.json file
            JsonObject env = JsonObject.fromFile("configs/torrent-handler-env.json");
            String torrentFolder = ((JsonString) env.get("torrentFolder")).getValue();
            String downloadFolder = ((JsonString) env.get("downloadFolder")).getValue();
            String logServerHost = ((JsonString) env.get("logServerHost")).getValue();
            int logServerPort = Integer.parseInt(((JsonString) env.get("logServerHost")).getValue());

            Log log = Log.getInstance();
            log.configure(logServerHost, logServerPort, "Torrent Handler");

            //if torrent folder doesnt exist create it
            File dir = new File(torrentFolder);
            if(!dir.exists()){
                log.info("creating torrent folder");
                dir.mkdir();
            }

            //if download folder doesnt exist create it
            File dir2 = new File(downloadFolder);
            if(!dir2.exists()){
                log.info("creating download folder");
                dir2.mkdir();
            }

            //load mirrors.json
            JsonObject config = JsonObject.fromFile("configs/mirrors.json");

            //scrape torrents from torrent webpages
            Thread torrentScrapeThread = new Thread( new ScrapeTorrents((JsonArray) config.get("torrents"), torrentFolder));
            torrentScrapeThread.start();
            log.info("Scrape Torrents Thread Started");

            //sync torrents into the torrent and download directorys from elsewhere in the storage directory
            Thread syncTorrentsThread = new Thread( new SyncTorrents((JsonObject) config.get("mirrors"), torrentFolder, downloadFolder));
            syncTorrentsThread.start();
            log.info("Sync Torrents Thread Started");
            
            //join threads
            syncTorrentsThread.join();
            log.info("Sync Torrents Thread Stopped");
            torrentScrapeThread.join();
            log.info("Scrape Torrent Thread Stopped");

            //sleep till 1am the next day
            LocalDateTime currientDateTime = LocalDateTime.now();
            LocalDate targetDate = LocalDate.now().plusDays(1);
            LocalTime targetTime = LocalTime.of(1, 0);
            LocalDateTime targetDateTime = LocalDateTime.of(targetDate, targetTime);
            long millsToSleep = ChronoUnit.MILLIS.between(currientDateTime, targetDateTime);
            Thread.sleep(millsToSleep);
        }
        catch(FileNotFoundException | ParseException | InterruptedException e){
            e.printStackTrace();
        }
    }
}
