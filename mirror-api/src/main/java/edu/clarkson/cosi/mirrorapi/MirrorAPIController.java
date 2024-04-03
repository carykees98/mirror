package edu.clarkson.cosi.mirrorapi;

import edu.clarkson.cosi.mirrorapi.error.NotFoundException;
import edu.clarkson.cosi.mirrorapi.io.Log;
import edu.clarkson.cosi.mirrorapi.state.Mirrors;
import org.lavajuno.lucidjson.JsonObject;
import org.lavajuno.lucidjson.error.JsonParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Defines available API mappings and their behavior,
 */
@RestController
@SuppressWarnings("unused")
public class MirrorAPIController {
    private static final String LOG_HOST = "mirrorlog";
    private static final int LOG_PORT = 4001;

    private final Mirrors mirrors;
    private final String mirrors_cached;
    private final Log log;

    /**
     * Constructs a MirrorAPIController.
     * @throws IOException If reading mirrors.json fails
     * @throws JsonParseException If parsing mirrors.json fails
     */
    public MirrorAPIController() throws IOException, JsonParseException {
        log = Log.getInstance();
        log.configure(LOG_HOST, LOG_PORT, "API");
        log.info("Reading mirrors...");
        JsonObject root = JsonObject.from(
                Files.readString(Path.of("configs/mirrors.json"))
        );
        mirrors = new Mirrors();
        mirrors.fromJsonObject((JsonObject) root.get("mirrors"));
        mirrors_cached = mirrors.toJsonString(); // cache mirror list response
        log.info("Started API controller.");
    }

    /**
     * Gets the list of all mirrors.
     * @return List of mirrors as JSON
     */
    @GetMapping("/api/mirrors")
    public String getMirrors() {
        log.info("Request for mirror list.");
        return mirrors_cached;
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
            return mirrors.get(key).toJsonString();
        } catch(NullPointerException e) {
            log.warn("Mirror \"" + key + "\" not found.");
            throw new NotFoundException();
        }
    }
}
