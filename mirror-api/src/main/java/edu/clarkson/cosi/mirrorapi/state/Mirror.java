package edu.clarkson.cosi.mirrorapi.state;

import org.lavajuno.lucidjson.JsonLiteral;
import org.lavajuno.lucidjson.JsonObject;
import org.lavajuno.lucidjson.JsonSerializable;
import org.lavajuno.lucidjson.JsonString;

public class Mirror implements JsonSerializable {
    private String name;
    private String page;
    private boolean official;
    private String homepage;
    private String color;
    private boolean publicRsync;
    private String alternative;
    private String icon;

    /**
     * Constructs an instance of Mirror with default values.
     */
    public Mirror() {
        name = "";
        page = "";
        official = false;
        homepage = "";
        color = "";
        publicRsync = false;
        alternative = "";
        icon = "";
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject o = new JsonObject();
        o.put("name", new JsonString(name));
        o.put("page", new JsonString(page));
        o.put("official", new JsonLiteral(official));
        o.put("homepage", new JsonString(homepage));
        o.put("color", new JsonString(color));
        o.put("publicRsync", new JsonLiteral(publicRsync));
        o.put("alternative", new JsonString(alternative));
        o.put("icon", new JsonString(icon));
        return o;
    }

    @Override
    public void fromJsonObject(JsonObject o) {
        name = ((JsonString) o.get("name")).value();
        page = ((JsonString) o.get("page")).value();
        official = ((JsonLiteral) o.get("official")).value();
        homepage = ((JsonString) o.get("homepage")).value();
        color = ((JsonString) o.get("color")).value();
        publicRsync = ((JsonLiteral) o.get("publicRsync")).value();
        try {
            alternative = ((JsonString) o.get("alternative")).value();
        } catch(NullPointerException ignored) {}

        try {
            icon = ((JsonString) o.get("icon")).value();
        } catch(NullPointerException ignored) {}
    }
}
