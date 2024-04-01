package mirrormap.io;
import mirrormap.util.Index;
import mirrormap.util.Pair;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class HttpRequest {
    private final String request;
    private final Map<String, String> headers;

    public HttpRequest(InputStream in) throws ParseException {
        Scanner scanner = new Scanner(in, StandardCharsets.UTF_8);
        String raw_request = scanner.useDelimiter("\\r\\n\\r\\n").next();
        Index i = new Index();
        request = parseLine(raw_request, i);
        headers = new TreeMap<>();
        while(i.pos < raw_request.length()) {
            Pair<String, String> header = parseHeader(raw_request, i);
            headers.put(header.first, header.second);
        }
    }

    public String getHeader(String key) { return headers.get(key); }

    public String getRequest() { return request; }

    private static Pair<String, String> parseHeader(String s, Index i) throws ParseException {
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();

        // Read key
        for(; i.pos < s.length(); i.pos++) {
            if(isDelimiter(s, i.pos)) {
                i.pos += 2;
                break;
            } else if(isLineBreak(s, i.pos)) {
                throw new ParseException("Parsing HTTP header - encountered line break", i.pos);
            }

            if(!isKeyCharacter(s.charAt(i.pos))) {
                throw new ParseException("Parsing HTTP header - invalid character in key.", i.pos);
            }
            key.append(s.charAt(i.pos));
        }

        // Read value
        for(; i.pos < s.length(); i.pos++) {
            if(isLineBreak(s, i.pos)) {
                i.pos += 2;
                break;
            }
            value.append(s.charAt(i.pos));

        }

        return new Pair<>(key.toString(), value.toString());
    }

    private static String parseLine(String s, Index i) throws ParseException {
        StringBuilder sb = new StringBuilder();
        for(; i.pos < s.length(); i.pos++) {
            if(isLineBreak(s, i.pos)) {
                i.pos += 2;
                return sb.toString();
            }
            sb.append(s.charAt(i.pos));
        }
        throw new ParseException("Parsing HTTP request - reached end of input", i.pos);
    }

    private static boolean isKeyCharacter(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '-';
    }

    private static boolean isDelimiter(String s, int i) {
        if(i >= s.length() - 1) { return false; }
        return s.charAt(i) == ':' && s.charAt(i + 1) == ' ';
    }

    private static boolean isLineBreak(String s, int i) {
        if(i >= s.length() - 1) { return false; }
        return s.charAt(i) == '\r' && s.charAt(i + 1) == '\n';
    }
}
