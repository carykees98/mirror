package mirrormap.io;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

/**
 * Represents a single websocket frame.
 */
public class WebsocketFrame {
    private final byte opcode;
    private final byte[] payload;

    /**
     * Constructs a WebsocketFrame.
     * @param opcode Opcode for this Websocket frame (see the websocket spec)
     * @param payload Payload for this Websocket frame (max. length 125 bytes)
     */
    public WebsocketFrame(byte opcode, byte[] payload) {
        if(opcode != 0x1 && opcode != 0x2 && opcode != 0x9 && opcode != 0xA) {
            throw new InvalidParameterException("Invalid opcode.");
        }
        if(payload.length > 125) {
            throw new InvalidParameterException("Payload is too large.");
        }
        this.opcode = opcode;
        this.payload = payload;
    }

    public WebsocketFrame(InputStream in) throws IOException {
        byte[] buf = new byte[125];
        opcode = (byte) (readByte(in) & 0x0F); // read opcode
        byte payload_len = (byte) (readByte(in) & 0x7F); // read payload length
        if(payload_len > 125) { throw new IOException("Payload too large."); }
        for(int i = 0; i < 4; i++) { readByte(in); } // ignore key
        for(int i = 0; i < payload_len; i++) { // read payload
            buf[i] = readByte(in);
        }
        payload = buf;
    }

    /**
     * @return This WebsocketFrame as bytes
     */
    public byte[] toBytes() {
        byte[] b = new byte[2 + payload.length];
        b[0] = (byte) (0x80 | (opcode & 0x0F));
        b[1] = (byte) (payload.length & 0x7F);
        if(payload.length > 0) {
            System.arraycopy(payload, 0, b, 2, payload.length);
        }
        return b;
    }

    public byte[] getPayload() { return payload; }

    private byte readByte(InputStream in) throws IOException {
        int b = in.read();
        if(b == -1) { throw new IOException("Unexpected end of stream."); }
        return (byte) b;
    }
}
