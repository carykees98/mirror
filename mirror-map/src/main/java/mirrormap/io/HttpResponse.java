package mirrormap.io;

import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.TreeMap;

public class HttpResponse {
    private static final String HTTP_VERSION = "HTTP/1.1 ";

    private final String status;

    private final Map<String, String> headers;

    public HttpResponse(String status) {
        this.status = status;
        this.headers = new TreeMap<>();
    }

    public void setHeader(String key, String value) throws InvalidParameterException {
        if(!key.matches("^[A-Za-z0-9_-]+$")) {
            throw new InvalidParameterException("Invalid header.");
        }
        headers.put(key, value);
    }

    public byte[] toBytes() {
        StringBuilder sb = new StringBuilder();
        sb.append(HTTP_VERSION).append(status).append("\r\n");
        for(Map.Entry<String, String> i : headers.entrySet()) {
            sb.append(i.getKey()).append(": ").append(i.getValue()).append("\r\n");
        }
        sb.append("\r\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
