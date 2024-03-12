package org.lavajuno.lucidjson;

import org.lavajuno.lucidjson.util.Index;

import java.text.ParseException;

/**
 * Represents a JSON string value.
 * Provides functionality for getting and setting the value.
 */
@SuppressWarnings("unused")
public class JsonString extends JsonEntity {
    private String value;

    /**
     * Constructs a JsonValue by parsing the input.
     * @param text JSON string to parse
     */
    public JsonString(String text, Index i) throws ParseException {
        skipSpace(text, i);
        if(text.charAt(i.pos) != '"') {
            throwParseError(text, i.pos, "Parsing string, expected a '\"'.");
        }
        i.pos++;
        int begin = i.pos;
        while(i.pos < text.length()) {
            if(text.charAt(i.pos) == '"' && text.charAt(i.pos - 1) != '\\') {
                break;
            }
            i.pos++;
        }
        if(i.pos == text.length()) {
            throwParseError(text, i.pos, "Parsing string, reached end of input.");
        }
        value = text.substring(begin, i.pos);
        i.pos++;
    }

    /**
     * Gets the value of this JsonString.
     * @return Value of this JsonString
     */
    public String getValue() { return value; }

    /**
     * Sets the value of this JsonString
     * @param value Value of this JsonString
     */
    public void setValue(String value) { this.value = value; }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

    @Override
    protected String toString(int indent) { return this.toString(); }
}
