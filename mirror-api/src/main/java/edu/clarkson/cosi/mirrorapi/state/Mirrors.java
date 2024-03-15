package edu.clarkson.cosi.mirrorapi.state;

import org.lavajuno.lucidjson.JsonObject;
import org.lavajuno.lucidjson.JsonSerializable;

import java.util.Collection;
import java.util.TreeMap;

/**
 * Mirrors stores a map of projects that we mirror.
 * It can be queried for single mirrors, or serialized to JSON.
 */
public class Mirrors implements JsonSerializable {
    private final TreeMap<String, Mirror> mirrors_map;

    /**
     * Constructs an instance of Mirrors with an empty map.
     */
    public Mirrors() { mirrors_map = new TreeMap<>(); }

    /**
     * Gets the Mirror with the specified key
     * @param key Key of the Mirror to get
     * @return The Mirror at the given key, or null if it does not exist.
     */
    public Mirror get(String key) { return mirrors_map.get(key); }

    @Override
    public JsonObject toJsonObject() {
        JsonObject o = new JsonObject();
        for(String s : mirrors_map.keySet()) {
            o.put(s, mirrors_map.get(s).toJsonObject());
        }
        return o;
    }

    @Override
    public void fromJsonObject(JsonObject o) {
        mirrors_map.clear();
        Collection<String> short_names = o.keys();
        for(String s : short_names) {
            Mirror mirror = new Mirror();
            mirror.fromJsonObject((JsonObject) o.get(s));
            mirrors_map.put(s, mirror);
        }
    }
}
