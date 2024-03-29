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
        System.err.println(explanation);
        System.err.println(); // extra newline
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
        skipSpace(text, i);
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
        skipSpace(text, i);
        String key = (new JsonString(text, i)).value();
        skipSpace(text, i);
        if(text.charAt(i.pos) != ':') {
            throwParseError(text, i.pos, "Parsing pair, expected a ':'.");
        }
        i.pos++;
        JsonEntity value = parseEntity(text, i);
        return new Pair<>(key, value);
    }

    /**
     * @param c Character to check
     * @return True if the character is whitespace (space, tab, or newline)
     */
    protected static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }

    /**
     * Advances the index past any whitespace
     * @param text Text to scan
     * @param i Index of next character to parse
     */
    protected static void skipSpace(String text, Index i) {
        while(isWhitespace(text.charAt(i.pos))) { i.pos++; }
    }

    /**
     * Serializes this JsonEntity to a JSON string with newlines and indentation.
     * @param indent Indent of this JsonEntity
     * @return This JsonEntity as a String
     */
    protected abstract String toJsonString(int indent);

    /**
     * Serializes this JsonEntity to a JSON string.
     * @param pretty false to minify, true to use newlines and indents
     * @return This JsonEntity as a String
     */
    public String toJsonString(boolean pretty) {
        return pretty ? this.toJsonString(0) : this.toJsonString();
    }

    /**
     * Serializes this JsonEntity to a minified JSON string.
     * @return This JsonEntity as a JSON string
     */
    public abstract String toJsonString();
}
