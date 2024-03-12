package edu.clarkson.cosi.mirrorapi;

import edu.clarkson.cosi.mirrorapi.error.NotFoundException;
import edu.clarkson.cosi.mirrorapi.io.Log;
import org.lavajuno.lucidjson.JsonArray;
import org.lavajuno.lucidjson.JsonObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;

/**
 * Defines available API mappings and their behavior,
 */
@RestController
@SuppressWarnings("unused")
public class MirrorAPIController {
    private static final String LOG_HOST = "localhost";
    private static final int LOG_PORT = 4001;

    private final JsonObject mirrors;
    private final JsonArray torrents;
    private final Log log;

    /**
     * Constructs a MirrorAPIController.
     * @throws IOException If reading mirrors.json fails
     * @throws ParseException If parsing mirrors.json fails
     */
    public MirrorAPIController() throws IOException, ParseException {
        JsonObject root = JsonObject.fromFile("configs/mirrors.json");
        mirrors = (JsonObject) root.get("mirrors");
        torrents = (JsonArray) root.get("torrents");
        log = Log.getInstance();
        log.configure(LOG_HOST, LOG_PORT, "MirrorAPI");
        log.info("Started API controller.");
    }

    /**
     * Gets the list of all mirrors.
     * @return List of mirrors as JSON
     */
    @GetMapping("/api/mirrors")
    public String getMirrors() {
        log.info("Request for mirror list.");
        return mirrors.toString();
    }

    /**
     * Gets a specific mirror
     * @param key The mirror's key
     * @return The mirror as JSON, or a 404 if it is not found
     */
    @GetMapping("/api/mirrors/{key}")
    public String getMirror(@PathVariable String key) {
        log.info("Request for mirror \"" + key + "\".");
        try {
            return mirrors.get(key).toString();
        } catch(NullPointerException e) {
            log.warn("Mirror \"" + key + "\" not found.");
            throw new NotFoundException();
        }
    }

    /**
     * Gets a list of all torrents
     * @return List of all torrents as JSON
     */
    @GetMapping("/api/torrents")
    public String getTorrents() {
        log.info("Request for torrent list.");
        return torrents.toString();
    }

    /**
     * Gets a specific torrent
     * @param index The torrent's index
     * @return The torrent as JSON, or a 404 if it is not found.
     */
    @GetMapping("/api/torrents/{index}")
    public String getTorrent(@PathVariable int index) {
        log.info("Request for torrent \"" + index + "\".");
        try {
            return torrents.get(index).toString();
        } catch(NullPointerException e) {
            log.warn("Torrent \"" + index + "\" not found.");
            throw new NotFoundException();
        }

    }
}
