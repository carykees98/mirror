package mirrortorrent.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.lavajuno.lucidjson.JsonArray;
import org.lavajuno.lucidjson.JsonNumber;
import org.lavajuno.lucidjson.JsonObject;
import org.lavajuno.lucidjson.error.JsonParseException;

import mirrortorrent.io.Log;
import mirrortorrent.torrents.TorrentScraper;
import mirrortorrent.torrents.TorrentSyncer;

public class MirrorTorrentApplication {

    /**
     * Entry point for Mirror's torrent handler
     */
    public static void main(String[] args) {
        File torrentDirectory = null;
        File downloadDirectory = null;
        String logServerHost = null;
        int logServerPort = -1;

        try {
            final File environmentFile = new File("configs/torrent-handler-env.json");
            final String environmentJsonString = new String(Files.readAllBytes(environmentFile.toPath()));

            JsonObject environment = JsonObject.from(environmentJsonString);

            torrentDirectory = new File(environment.get("torrent_directory").toJsonString());
            downloadDirectory = new File(environment.get("download_directory").toJsonString());
            logServerHost = environment.get("log_server_host").toJsonString();
            logServerPort = ((JsonNumber) environment.get("log_server_port")).toInt();
        } catch (FileNotFoundException e) {
            System.out.println("Environmment file does not exist! Exiting.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Failed to read environment file! Exception message: " + e.getMessage());
            System.exit(1);
        } catch (JsonParseException e) {
            System.out.println("Failed to parse environment file! Exception message: " + e.getMessage());
            System.exit(1);
        }

        // Initialize Logger
        Log log = Log.getInstance();
        if (logServerPort != -1) {
            log.configure(logServerHost, logServerPort, "Torrent Handler");
        } else {
            System.out.println("Log Server Host or Port not set! Exiting.");
            System.exit(1);
        }

        // If torrents directory doesn't exist, create it
        if (!torrentDirectory.exists()) {
            log.info("Creating torrents directory");

            if (!torrentDirectory.mkdir()) {
                log.fatal("Failed to create torrents directory!");
                System.exit(1);
            }
        }

        // If downloads directory doesn't exist, create it.
        if (!downloadDirectory.exists()) {
            log.info("Creating downloads directory");

            if (!downloadDirectory.mkdir()) {
                log.fatal("Failed to create torrents directory!");
                System.exit(1);
            }
        }

        while (true) {
            JsonObject mirrorsConfig = new JsonObject();
            try {
                // Load mirrorsConfig from mirrors.json
                final File mirrorsJsonFile = new File("configs/torrent-handler-env.json");
                final String mirrorsJsonString = new String(Files.readAllBytes(mirrorsJsonFile.toPath()));

                mirrorsConfig = JsonObject.from(mirrorsJsonString);
            } catch (IOException | JsonParseException e) {
                log.fatal(e.getMessage());
                System.exit(1);
            }

            // Spawn Torrent Scraper Thread
            final Thread torrentScrapeThread = new Thread(
                    new TorrentScraper((JsonArray) mirrorsConfig.get("torrents"), torrentDirectory));
            torrentScrapeThread.start();
            log.info("Scrape Torrents Thread Started");

            // Spawn Torrent Syncing Thread
            final Thread syncTorrentsThread = new Thread(
                    new TorrentSyncer((JsonObject) mirrorsConfig.get("mirrors"), torrentDirectory, downloadDirectory));
            syncTorrentsThread.start();
            log.info("Sync Torrents Thread Started");

            try {
                // Join Threads Upon Completion
                syncTorrentsThread.join();
                log.info("Joined Sync Torrents Thread");
                torrentScrapeThread.join();
                log.info("Joined Torrent Scraper Thread");

                // Sleep Until 1am The Following Day
                LocalDateTime timeToWake = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(1, 0));
                long millisecondsToSleep = ChronoUnit.MILLIS.between(LocalDateTime.now(), timeToWake);

                Thread.sleep(millisecondsToSleep);
            } catch (DateTimeException e) {
                log.warn(e.getMessage());
            } catch (InterruptedException e) {
                log.fatal(e.getMessage());
                System.exit(1);
            }
        }
    }
}
