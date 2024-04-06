package mirrormap.io;

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
}
