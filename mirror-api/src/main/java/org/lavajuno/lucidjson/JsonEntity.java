package org.lavajuno.lucidjson;

import org.lavajuno.lucidjson.util.Index;
import org.lavajuno.lucidjson.util.Pair;
import java.text.ParseException;

/**
 * Abstract representation of a single JSON entity.
 * Instances of JsonEntity can be objects, arrays, strings, numbers, or literals.
 * JsonEntity provides functionality for validating and parsing JSON for its inheritors.
 */
@SuppressWarnings("unused")
public abstract class JsonEntity {
    /**
     * Prints a parse error to stderr, then throws a ParseException
     * @param text The input currently being parsed
     * @param pos Index of the character that caused error
     * @param explanation Why the parse error happened
     */
    protected static void throwParseError(String text, int pos, String explanation)
            throws ParseException {
        System.err.println("JSON - Parse error at index " + pos + " of input:");
        System.err.print(text.substring(pos, Math.min(pos + 12, text.length())));
        System.err.println("...");
        System.err.println("^");
        System.err.println(explanation + "\n"); /* extra newline */
        throw new ParseException(explanation, pos);
    }

    /**
     * Constructs a single JsonEntity by parsing the input.
     * @param text Text to parse
     * @param i Index of next character to parse
     * @return JsonEntity created from the input.
     * @throws ParseException If the input does not match any type of entity
     */
    protected static JsonEntity parseEntity(String text, Index i) throws ParseException {
        while(text.charAt(i.pos) == ' ' || text.charAt(i.pos) == '\t') { i.pos++; }
        return switch(text.charAt(i.pos)) {
            case '{' -> new JsonObject(text, i);
            case '[' -> new JsonArray(text, i);
            case '"' -> new JsonString(text, i);
            case 't', 'f', 'n' -> new JsonLiteral(text, i);
            default -> new JsonNumber(text, i);
        };
    }

    /**
     * Constructs a key-value pair (String : JsonEntity) by parsing the input.
     * @param text Text to parse
     * @param i Index of next character to parse
     * @return Key-value pair created from the input.
     * @throws ParseException If the input does not match a pair containing a String and JsonEntity
     */
    protected static Pair<String, JsonEntity> parsePair(String text, Index i) throws ParseException {
        while(text.charAt(i.pos) == ' ' || text.charAt(i.pos) == '\t') { i.pos++; }
        String key = (new JsonString(text, i)).getValue();
        skipSpace(text, i);
        if(text.charAt(i.pos) != ':') {
            throwParseError(text, i.pos, "Parsing pair, expected a ':'.");
        }
        i.pos++;
        JsonEntity value = parseEntity(text, i);
        return new Pair<>(key, value);
    }

    /**
     * Advances the index past any whitespace
     * @param text Text to scan
     * @param i Index of next character to parse
     */
    protected static void skipSpace(String text, Index i) {
        while(text.charAt(i.pos) == ' ' || text.charAt(i.pos) == '\t') { i.pos++; }
    }

    /**
     * Serializes this JsonEntity to a String with newlines and indentation.
     * @param indent Indent of this JsonEntity
     * @return This JsonEntity as a String
     */
    protected abstract String toString(int indent);

    /**
     * Serializes this JsonEntity to a String with optional formatting.
     * @param pretty Whether to use newlines and indents in the output
     * @return This JsonEntity as a String
     */
    public String toString(boolean pretty) {
        return pretty ? this.toString(0) : this.toString();
    }

    /**
     * Serializes this JsonEntity to a String without any formatting.
     * @return This JsonEntity as a String
     */
    @Override
    public abstract String toString();
}
